package liquibase.ext.percona;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liquibase.database.Database;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.sql.Sql;
import liquibase.statement.core.RuntimeStatement;
import liquibase.util.StreamUtil;

/**
 * Statement to run {@code pt-online-schema-change} in order
 * to alter a database table.
 */
public class PTOnlineSchemaChangeStatement extends RuntimeStatement {
    public static final String COMMAND = "pt-online-schema-change";
    static PerconaToolkitVersion perconaToolkitVersion = null;
    static Boolean available = null;

    private static Logger log = LogFactory.getInstance().getLog();

    private String databaseName;
    private String tableName;
    private String alterStatement;

    public PTOnlineSchemaChangeStatement(String databaseName, String tableName, String alterStatement) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.alterStatement = alterStatement;
    }

    /**
     * Tokenizes the given options into separate arguments, so that it can be
     * fed into the {@link ProcessBuilder}'s commands.
     * @param options the options as one single string
     * @return the list of arguments
     */
    private List<String> tokenize(String options) {
        StringTokenizer stringTokenizer = new StringTokenizer(options);
        List<String> result = new LinkedList<String>();
        while (stringTokenizer.hasMoreTokens()) {
            result.add(stringTokenizer.nextToken());
        }
        return joinQuotedArguments(result);
    }

    /**
     * Very simplistic approach to join together any quoted arguments.
     * Only double quotes are supported and the join character is a space.
     * @param tokenizedArguments the arguments tokenized by space
     * @return the filtered arguments, maybe joined
     */
    private List<String> joinQuotedArguments(List<String> tokenizedArguments) {
        final String joinCharacters = " ";
        List<String> filtered = new LinkedList<String>();
        boolean inQuotes = false;
        for (int i = 0; i < tokenizedArguments.size(); i++) {
            String arg = tokenizedArguments.get(i);
            if (!inQuotes) {
                if (arg.startsWith("\"")) {
                    inQuotes = true;
                    arg = arg.substring(1);
                }
                if (arg.endsWith("\"")) {
                    inQuotes = false;
                    arg = arg.substring(0, arg.length() - 1);
                }
                filtered.add(arg);
            } else {
                if (arg.endsWith("\"")) {
                    inQuotes = false;
                    arg = arg.substring(0, arg.length() - 1);
                }
                String last = filtered.get(filtered.size() - 1);
                filtered.set(filtered.size() - 1, last + joinCharacters + arg);
            }
        }
        return filtered;
    }

    /**
     * Builds the command line arguments that will be executed.
     * @param database the database - needed to get the connection info.
     * @return the command line arguments including {@link #COMMAND}
     */
    List<String> buildCommand(Database database) {
        List<String> commands = new ArrayList<String>();
        commands.add(getFullToolkitPath());

        // must be the first on the command line, otherwise "--config" cannot be used
        if (!Configuration.getAdditionalOptions().isEmpty()) {
            commands.addAll(tokenize(Configuration.getAdditionalOptions()));
        }

        commands.add("--alter=" + alterStatement);

        if (database.getConnection() != null) {
            DatabaseConnectionUtil connection = new DatabaseConnectionUtil(database.getConnection());
            commands.add("--host=" + connection.getHost());
            commands.add("--port=" + connection.getPort());
            commands.add("--user=" + connection.getUser());
            String pw = connection.getPassword();
            if (pw != null) {
                commands.add("--password=" + pw);
            }
        }

        commands.add("--execute");
        if (databaseName != null) {
            commands.add("D=" + databaseName + ",t=" + tableName);
        } else {
            commands.add("D=" + database.getLiquibaseSchemaName() + ",t=" + tableName);
        }
        return commands;
    }

    /**
     * Generates the command line that would be executed and return it as a single string.
     * The password will be masked.
     * @param database the database - needed to get the connection info
     * @return the string
     */
    public String printCommand(Database database) {
        List<String> command = buildCommand(database);
        return filterCommands(command);
    }

    /**
     * Converts the given command list into a single string and mask the password
     * @param command the command line arguments that would be used for pt-online-schema-change
     * @return the string with masked password
     */
    private String filterCommands(List<String> command) {
        StringBuilder sb = new StringBuilder();
        for (String s : command) {
            sb.append(" ");
            if (s.startsWith("--password")) {
                sb.append("--password=***");
            } else if (s.contains(" ")) {
                sb.append(s.substring(0, s.indexOf('=') + 1)).append("\"").append(s.substring(s.indexOf('=') + 1))
                        .append("\"");
            } else {
                sb.append(s);
            }
        }
        return sb.substring(1).toString();
    }

    /**
     * Actually executes pt-online-schema change. Does not generate any Sql.
     * @return always <code>null</code>
     */
    @Override
    public Sql[] generate(Database database) {
        List<String> cmndline = buildCommand(database);
        log.info("Executing: " + filterCommands(cmndline));

        ProcessBuilder pb = new ProcessBuilder(cmndline);
        if (Configuration.isPerconaToolkitDebug()) {
            pb.environment().put("PTDEBUG", "1");
        }
        pb.redirectErrorStream(true);
        Process p = null;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final OutputStream tee = new FilterOutputStream(outputStream) {
            @Override
            public void write(int b) throws IOException {
                if (b == '\n') {
                    log.info(outputStream.toString(Charset.defaultCharset().toString()));
                    outputStream.reset();
                } else {
                    super.write(b);
                }
            }
        };
        try {
            p = pb.start();
            final InputStream in = p.getInputStream();
            final InputStream err = p.getErrorStream();

            IOThread reader = new IOThread(in, tee);
            IOThread reader2 = new IOThread(err, tee);
            reader.start();
            reader2.start();

            int exitCode = p.waitFor();
            reader.join(5000);
            reader2.join(5000);
            // log the remaining output
            log.info(outputStream.toString(Charset.defaultCharset().toString()));

            if (exitCode != 0) {
                throw new RuntimeException("Percona exited with " + exitCode);
            }
        } catch (IOException e) {
            throw new UnexpectedLiquibaseException(e);
        } catch (InterruptedException e) {
            throw new UnexpectedLiquibaseException(e);
        } finally {
            if (p != null) {
                StreamUtil.closeQuietly(p.getErrorStream());
                StreamUtil.closeQuietly(p.getInputStream());
                StreamUtil.closeQuietly(p.getOutputStream());
                p.destroy();
            }
            StreamUtil.closeQuietly(outputStream);
        }
        return null;
    }

    @Override
    public String toString() {
        return PTOnlineSchemaChangeStatement.class.getSimpleName()
                + "[database: " + databaseName + ", table: " + tableName + ", alterStatement: " + alterStatement + "]";
    }

    private static class IOThread extends Thread {
        private Logger log = LogFactory.getInstance().getLog();
        private InputStream from;
        private OutputStream to;

        public IOThread(InputStream from, OutputStream to) {
            super();
            setDaemon(true);
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            try {
                StreamUtil.copy(from, to);
            } catch (IOException e) {
                log.debug("While copying streams", e);
            }
        }
    }

    public static synchronized PerconaToolkitVersion getVersion() {
        if (available == null) {
            checkIsAvailableAndGetVersion();
        }
        return perconaToolkitVersion != null ? perconaToolkitVersion : new PerconaToolkitVersion(null);
    }

    /**
     * Checks whether the command is available and can be started.
     * <p>
     * <em>Implementation detail:</em>
     * This is lazily detected once and then cached.
     * </p>
     * @return <code>true</code> if it is available and executable, <code>false</code> otherwise
     * @see #COMMAND
     */
    public static synchronized boolean isAvailable() {
        if (available != null) {
            return available.booleanValue();
        }
        checkIsAvailableAndGetVersion();
        return available.booleanValue();
    }

    private static String getFullToolkitPath() {
        String toolkitPath = Configuration.getPerconaToolkitPath();
        if (toolkitPath.isEmpty()) {
            return COMMAND;
        }
        if (toolkitPath.endsWith(File.separator)) {
            return toolkitPath + COMMAND;
        } else {
            return toolkitPath + File.separator + COMMAND;
        }
    }

    private static void checkIsAvailableAndGetVersion() {
        ProcessBuilder pb = new ProcessBuilder(getFullToolkitPath(), "--version");
        pb.redirectErrorStream(true);
        Process p = null;
        try {
            p = pb.start();
            p.waitFor();

            String output = StreamUtil.getStreamContents(p.getInputStream());
            if (output != null) {
                Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+)").matcher(output);
                if (matcher.find()) {
                    perconaToolkitVersion = new PerconaToolkitVersion(matcher.group(1));
                }
            }
            available = true;
            log.info("Using percona toolkit: " + perconaToolkitVersion);
        } catch (IOException e) {
            available = false;
        } catch (InterruptedException e) {
            available = false;
        } finally {
            if (p != null) {
                StreamUtil.closeQuietly(p.getErrorStream());
                StreamUtil.closeQuietly(p.getInputStream());
                StreamUtil.closeQuietly(p.getOutputStream());
                p.destroy();
            }
        }
    }
}

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.database.PreparedStatementFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.logging.Logger;
import liquibase.statement.ExecutablePreparedStatement;
import liquibase.util.StreamUtil;

/**
 * Statement to run {@code pt-online-schema-change} in order
 * to alter a database table.
 */
public class PTOnlineSchemaChangeStatement implements ExecutablePreparedStatement {
    public static final String COMMAND = "pt-online-schema-change";
    static PerconaToolkitVersion perconaToolkitVersion = null;
    static Boolean available = null;

    private static Logger log = Scope.getCurrentScope().getLog(PTOnlineSchemaChangeStatement.class);

    private Database database;
    private String databaseName;
    private String tableName;
    private String alterStatement;
    private Optional<String> perconaOptions;

    public PTOnlineSchemaChangeStatement(Database database, String databaseName, String tableName, String alterStatement,
                                         Optional<String> perconaOptions) {
        this.database = database;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.alterStatement = alterStatement;
        this.perconaOptions = perconaOptions;
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
                } else if (arg.contains("=\"")) {
                    inQuotes = true;
                    arg = arg.replaceFirst("=\"", "=");
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
        if (perconaOptions.isPresent()) {
            commands.addAll(tokenize(perconaOptions.get()));
        } else if (!Configuration.getAdditionalOptions().isEmpty()) {
            commands.addAll(tokenize(Configuration.getAdditionalOptions()));
        }

        commands.add("--alter=" + alterStatement);

        StringBuilder dsn = new StringBuilder(200);

        if (database.getConnection() != null) {
            DatabaseConnectionUtil connection = new DatabaseConnectionUtil(database.getConnection());
            dsn.append("h=").append(connection.getHost());
            dsn.append(",P=").append(connection.getPort());
            dsn.append(",u=").append(connection.getUser());
            if (connection.isSsl()) {
                dsn.append(",s=yes");
            }
            dsn.append(',');

            String pw = connection.getPassword();
            if (pw != null) {
                commands.add("--password=" + pw);
            }
        }
        if (databaseName != null) {
            dsn.append("D=").append(databaseName);
        } else {
            dsn.append("D=").append(database.getLiquibaseCatalogName());
        }
        dsn.append(",t=").append(tableName);

        commands.add("--execute");
        commands.add(dsn.toString());
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
            } else if (s.startsWith("--slave-password")) {
                sb.append("--slave-password=***");
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
    public void execute(PreparedStatementFactory factory) throws DatabaseException {
        List<String> cmndline = buildCommand(database);
        log.info("Executing: " + filterCommands(cmndline));

        KeepAliveThread keepAlive = new KeepAliveThread(database);
        if (Configuration.isKeepAlive()) {
            keepAlive.start();
        }

        ProcessBuilder pb = new ProcessBuilder(cmndline);
        if (Configuration.isPerconaToolkitDebug()) {
            pb.environment().put("PTDEBUG", "1");
        }
        pb.redirectErrorStream(true);
        Process p = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
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
            p = pb.start();
            try (InputStream in = p.getInputStream();
                 InputStream err = p.getErrorStream()) {
                IOThread reader = new IOThread(in, tee);
                IOThread reader2 = new IOThread(err, tee);
                reader.start();
                reader2.start();

                int exitCode = p.waitFor();
                reader.join(5000);
                reader2.join(5000);
                keepAlive.interrupt();
                // log the remaining output
                log.info(outputStream.toString(Charset.defaultCharset().toString()));

                if (exitCode != 0) {
                    throw new UnexpectedLiquibaseException("Percona exited with " + exitCode);
                }
            }
        } catch (IOException e) {
            throw new UnexpectedLiquibaseException(e);
        } catch (InterruptedException e) {
            throw new UnexpectedLiquibaseException(e);
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    @Override
    public String toString() {
        return PTOnlineSchemaChangeStatement.class.getSimpleName()
                + "[database: " + databaseName + ", table: " + tableName + ", alterStatement: " + alterStatement + "]";
    }

    @Override
    public boolean skipOnUnsupported() {
        return false;
    }

    @Override
    public boolean continueOnError() {
        return false;
    }

    private static class KeepAliveThread extends Thread {
        private final Database database;

        public KeepAliveThread(Database database) {
            this.database = database;
        }

        @Override
        public void run() {
            boolean running = true;
            JdbcConnection connection = (JdbcConnection) database.getConnection();
            long keepAlive = 28800L;

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("show variables where variable_name = 'wait_timeout'");
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        keepAlive = rs.getLong(2);
                    } else {
                        log.warning("Couldn't determine wait_timeout for keepAlive, using default");
                    }
                }
            } catch (SQLException | DatabaseException e) {
                log.warning("Couldn't determine wait_timeout for keepAlive, using default", e);
            }

            long sleepTimeInMillis = TimeUnit.SECONDS.toMillis(keepAlive) / 2;
            // make sure, we don't ping more often than every 500 ms.
            sleepTimeInMillis = Math.max(500, sleepTimeInMillis);
            log.info("KeepAlive every " + sleepTimeInMillis + " millis");

            while (running) {
                try (Statement stmt = connection.createStatement()) {
                    log.fine("Pinging database...");
                    stmt.execute("SELECT 1");
                    sleep(sleepTimeInMillis);
                } catch (SQLException | DatabaseException e) {
                    log.severe("Couldn't ping database", e);
                    running = false;
                } catch (InterruptedException e) {
                    currentThread().interrupt();
                    running = false;
                }
            }

            log.info("KeepAlive thread finished");
        }
    }

    private static class IOThread extends Thread {
        private Logger log = Scope.getCurrentScope().getLog(IOThread.class);
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
                log.fine("While copying streams", e);
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
            try (InputStream in = p.getInputStream()) {
                p.waitFor();
                String output = StreamUtil.readStreamAsString(in);
                if (output != null) {
                    Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+)").matcher(output);
                    if (matcher.find()) {
                        perconaToolkitVersion = new PerconaToolkitVersion(matcher.group(1));
                    }
                }
                available = true;
                log.info("Using percona toolkit: " + perconaToolkitVersion);
            }
        } catch (IOException e) {
            available = false;
        } catch (InterruptedException e) {
            available = false;
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }
}

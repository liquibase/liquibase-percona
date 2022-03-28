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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liquibase.Scope;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.logging.Logger;

/**
 * Wraps a {@link DatabaseConnection} to have easy access
 * to its connection properties like host, port, user and password.
 */
public class DatabaseConnectionUtil {
    private static final String DEFAULT_LIQUIBASE_PROPERTIES_FILENAME = "liquibase.properties";

    /** The name of the password property. */
    private static final String PASSWORD_PROPERTY_NAME = "password";

    private Logger log = Scope.getCurrentScope().getLog(DatabaseConnectionUtil.class);

    private final String host;
    private final String port;
    private final String user;
    private final String password;

    public DatabaseConnectionUtil(DatabaseConnection connection) {
        this.host = determineHost(connection.getURL());
        this.port = determinePort(connection.getURL());
        this.user = determineUser(connection.getConnectionUserName());
        this.password = determinePassword(connection);
    }

    public String getHost() {
        return this.host;
    }

    public String getPort() {
        return this.port;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    private static String determineHost(String url) {
        Pattern p = Pattern.compile("jdbc:(?:mysql|mariadb):(?:replication:|loadbalance:|sequential:|aurora:)?//([^@]+@)?([^:/]+)");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group(2);
        }
        return "";
    }

    private static String determinePort(String url) {
        Pattern p = Pattern.compile("jdbc:(?:mysql|mariadb):(?:replication:|loadbalance:|sequential:|aurora:)?//[^:/]+:(\\d+)");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return "3306";
    }

    private static String determineUser(String connectionUserName) {
        if (connectionUserName.contains("@")) {
            return connectionUserName.substring(0, connectionUserName.indexOf('@'));
        }
        return connectionUserName;
    }

    private String determinePassword(DatabaseConnection connection) {
        String liquibasePassword = Configuration.getLiquibasePassword();
        if (liquibasePassword != null) {
            return liquibasePassword;
        }
    
        if (connection instanceof JdbcConnection) {
            try {
                Connection jdbcCon = ((JdbcConnection) connection).getWrappedConnection();
                jdbcCon = getDelegatedDbcpConnection(jdbcCon);
                jdbcCon = getDelegatedDbcp2Connection(jdbcCon);
                jdbcCon = getUnderlyingJdbcConnectionFromProxy(jdbcCon);
    
                Class<?> connectionImplClass = ReflectionUtils.findClass(jdbcCon.getClass().getClassLoader(),
                        "com.mysql.jdbc.ConnectionImpl",   // MySQL Connector 5.1.38: com.mysql.jdbc.ConnectionImpl
                        "com.mysql.cj.jdbc.ConnectionImpl" // MySQL Connector 6.0.4: com.mysql.cj.jdbc.ConnectionImpl
                    );
    
                Class<?> mariadbConnection2Class = ReflectionUtils.findClass(jdbcCon.getClass().getClassLoader(),
                        "org.mariadb.jdbc.MariaDbConnection" // MariaDB Connector 2.x
                    );

                Class<?> mariadbConnection3Class = ReflectionUtils.findClass(jdbcCon.getClass().getClassLoader(),
                        "org.mariadb.jdbc.Connection"         // MariaDB Connector 3.x
                    );

                boolean isMySQL = false;
                boolean isMariaDB = false;
    
                if (connectionImplClass != null && connectionImplClass.isInstance(jdbcCon)) {
                    isMySQL = true;
                }
                if (mariadbConnection2Class != null && mariadbConnection2Class.isInstance(jdbcCon)
                        || mariadbConnection3Class != null && mariadbConnection3Class.isInstance(jdbcCon)) {
                    isMariaDB = true;
                }
    
                if (isMySQL) {
                    // ConnectionImpl stores the properties, and the jdbc connection is a subclass of it...
                    Properties props = ReflectionUtils.readField(connectionImplClass, jdbcCon, "props");
                    String password = props.getProperty(PASSWORD_PROPERTY_NAME);
                    if (password != null && !password.trim().isEmpty()) {
                        return password;
                    }
                } else if (isMariaDB && mariadbConnection2Class != null) {
                    // MariaDB Connector 2.x
                    Object protocol = ReflectionUtils.readField(mariadbConnection2Class, jdbcCon, "protocol");
                    Object urlParser = ReflectionUtils.invokeMethod(protocol.getClass(), protocol, "getUrlParser");
                    Object password = ReflectionUtils.invokeMethod(urlParser.getClass(), urlParser, "getPassword");
                    if (password != null && !password.toString().trim().isEmpty()) {
                        return password.toString();
                    }
                } else if (isMariaDB && mariadbConnection3Class != null) {
                    // MariaDB Connector 3.x
                    Object configuration = ReflectionUtils.readField(mariadbConnection3Class, jdbcCon, "conf");
                    Object password = ReflectionUtils.invokeMethod(configuration.getClass(), configuration, "password");
                    if (password != null && !password.toString().trim().isEmpty()) {
                        return password.toString();
                    }
                } else {
                    throw new RuntimeException("JdbcConnection is unsupported: " + jdbcCon.getClass().getName());
                }
            } catch (Exception e) {
                log.warning("Couldn't determine the password from JdbcConnection", e);
            }
        }
    
        try {
            Properties liquibaseProperties = loadLiquibaseProperties();
            if (liquibaseProperties.containsKey(PASSWORD_PROPERTY_NAME)) {
                return liquibaseProperties.getProperty(PASSWORD_PROPERTY_NAME);
            }
        } catch (IOException e) {
            log.warning("Couldn't read " + DEFAULT_LIQUIBASE_PROPERTIES_FILENAME + " file", e);
        }
    
        return null;
    }

    /**
     * Unwraps the underlying jdbc connection from a tomcat-jdbc pooled connection.
     * @param wrappedConnection the proxy
     * @return the unwrapped jdbc connection or, if the connection cannot be unwrapped, the wrapped connection itself.
     */
    private Connection getUnderlyingJdbcConnectionFromProxy(Connection wrappedConnection) {
        if (Proxy.isProxyClass(wrappedConnection.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(wrappedConnection);
            Class<?> pooledConnectionClass = ReflectionUtils.loadClass("org.apache.tomcat.jdbc.pool.PooledConnection", invocationHandler.getClass().getClassLoader());

            if (pooledConnectionClass != null) {
                try {
                    Object pooledConnectionInstance = wrappedConnection.unwrap(pooledConnectionClass);
                    Connection result = ReflectionUtils.invokeMethod(pooledConnectionClass, pooledConnectionInstance, "getConnection");
                    return result != null ? result : wrappedConnection;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                log.warning("Couldn't determine the password from JdbcConnection. "
                        + "It is a not supported proxy class: " + invocationHandler.getClass().getName());
            }
        }
        return wrappedConnection;
    }

    private Connection getDelegatedDbcpConnection(Connection con) {
        Connection result = ReflectionUtils.invokeMethod("org.apache.commons.dbcp.DelegatingConnection",
                con, "getInnermostDelegateInternal");
        return result != null ? result : con;
    }

    private Connection getDelegatedDbcp2Connection(Connection con) {
        Connection result = ReflectionUtils.invokeMethod("org.apache.commons.dbcp2.DelegatingConnection",
                con, "getInnermostDelegateInternal");
        return result != null ? result : con;
    }

    private Properties loadLiquibaseProperties() throws IOException {
        Properties properties = new Properties();

        File propertiesFile = new File(DEFAULT_LIQUIBASE_PROPERTIES_FILENAME);
        if (propertiesFile.exists()) {
            FileInputStream stream = new FileInputStream(propertiesFile);
            try {
                properties.load(stream);
            } finally {
                stream.close();
            }
        } else {
            InputStream stream = DatabaseConnectionUtil.class.getClassLoader()
                    .getResourceAsStream(DEFAULT_LIQUIBASE_PROPERTIES_FILENAME);
            if (stream != null) {
                try {
                    properties.load(stream);
                } finally {
                    stream.close();
                }
            }
        }
        return properties;
    }
}

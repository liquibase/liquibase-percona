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

import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;

/**
 * Wraps a {@link DatabaseConnection} to have easy access
 * to its connection properties like host, port, user and password.
 */
public class DatabaseConnectionUtil {
    private static final String DEFAULT_LIQUIBASE_PROPERTIES_FILENAME = "liquibase.properties";

    /** The name of the password property. */
    private static final String PASSWORD_PROPERTY_NAME = "password";

    private Logger log = LogFactory.getInstance().getLog();

    private DatabaseConnection connection;

    public DatabaseConnectionUtil(DatabaseConnection connection) {
        this.connection = connection;
    }

    public String getHost() {
        Pattern p = Pattern.compile("jdbc:mysql://([^@]+@)?([^:/]+)");
        Matcher m = p.matcher(connection.getURL());
        if (m.find()) {
            return m.group(2);
        }
        return "";
    }

    public String getPort() {
        Pattern p = Pattern.compile("jdbc:mysql://[^:/]+:(\\d+)");
        Matcher m = p.matcher(connection.getURL());
        if (m.find()) {
            return m.group(1);
        }
        return "3306";
    }

    public String getUser() {
        String connectionUserName = connection.getConnectionUserName();
        if (connectionUserName.contains("@")) {
            return connectionUserName.substring(0, connectionUserName.indexOf('@'));
        }
        return connectionUserName;
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

    public String getPassword() {
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

                // Unknown MySQL Connector version?
                if (connectionImplClass == null) {
                    throw new RuntimeException("Couldn't find class ConnectionImpl");
                }

                if (!connectionImplClass.isInstance(jdbcCon)) {
                    throw new RuntimeException("JdbcConnection is unsupported: " + jdbcCon.getClass().getName());
                }

                // ConnectionImpl stores the properties, and the jdbc connection is a subclass of it...
                Properties props = ReflectionUtils.readField(connectionImplClass, jdbcCon, "props");
                String password = props.getProperty(PASSWORD_PROPERTY_NAME);
                if (password != null && !password.trim().isEmpty()) {
                    return password;
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

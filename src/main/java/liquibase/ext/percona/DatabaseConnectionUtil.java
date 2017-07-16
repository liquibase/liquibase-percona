package liquibase.ext.percona;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    static String passwordForTests = null;

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

    private static Class<?> loadClass(String name, ClassLoader loader) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Unwraps the underlying jdbc connection from a tomcat-jdbc pooled connection.
     * @param wrappedConnection the proxy
     * @return the unwrapped jdbc connection or, if the connection cannot be unwrapped, the wrapped connection itself.
     */
    private Connection getUnderlyingJdbcConnectionFromProxy(Connection wrappedConnection) {
        if (Proxy.isProxyClass(wrappedConnection.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(wrappedConnection);
            Class<?> pooledConnectionClass = loadClass("org.apache.tomcat.jdbc.pool.PooledConnection", invocationHandler.getClass().getClassLoader());

            if (pooledConnectionClass != null) {
                try {
                    Object pooledConnectionInstance = wrappedConnection.unwrap(pooledConnectionClass);
                    Method getJdbcConnectionMethod = pooledConnectionClass.getMethod("getConnection");
                    return (Connection) getJdbcConnectionMethod.invoke(pooledConnectionInstance);
                } catch (NoSuchMethodException e) {
                    log.warning("Couldn't determine the password from JdbcConnection", e);
                } catch (SecurityException e) {
                    log.warning("Couldn't determine the password from JdbcConnection", e);
                } catch (IllegalAccessException e) {
                    log.warning("Couldn't determine the password from JdbcConnection", e);
                } catch (IllegalArgumentException e) {
                    log.warning("Couldn't determine the password from JdbcConnection", e);
                } catch (InvocationTargetException e) {
                    log.warning("Couldn't determine the password from JdbcConnection", e);
                } catch (SQLException e) {
                    log.warning("Couldn't determine the password from JdbcConnection", e);
                }
            } else {
                log.warning("Couldn't determine the password from JdbcConnection. "
                        + "It is a not supported proxy class: " + invocationHandler.getClass().getName());
            }
        }
        return wrappedConnection;
    }

    public String getPassword() {
        if (connection instanceof JdbcConnection) {
            Connection wrappedConnection = ((JdbcConnection) connection).getWrappedConnection();
            Connection jdbcCon = getUnderlyingJdbcConnectionFromProxy(wrappedConnection);

            try {
                Class<?> connectionImplClass = null;

                // MySQL Connector 5.1.38
                connectionImplClass = loadClass("com.mysql.jdbc.ConnectionImpl", jdbcCon.getClass().getClassLoader());

                // MySQL Connector 6.0.4
                if (connectionImplClass == null) {
                    connectionImplClass = loadClass("com.mysql.cj.jdbc.ConnectionImpl", jdbcCon.getClass().getClassLoader());
                }

                // Unknown MySQL Connector version?
                if (connectionImplClass == null) {
                    throw new RuntimeException("Couldn't find class ConnectionImpl");
                }

                if (!connectionImplClass.isInstance(jdbcCon)) {
                    throw new RuntimeException("JdbcConnection is unsupported: " + jdbcCon.getClass().getName());
                }

                // ConnectionImpl stores the properties, and the jdbc connection is a subclass of it...
                Field propsField = connectionImplClass.getDeclaredField("props");
                propsField.setAccessible(true);
                Properties props = (Properties) propsField.get(jdbcCon);
                String password = props.getProperty(PASSWORD_PROPERTY_NAME);
                if (password != null && !password.trim().isEmpty()) {
                    return password;
                }
            } catch (Exception e) {
                log.warning("Couldn't determine the password from JdbcConnection", e);
            }
        } else if (passwordForTests != null) {
            log.warning("Using passwordForTests");
            return passwordForTests;
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

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
import java.lang.reflect.Field;
import java.sql.Connection;
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

    public String getPassword() {
        if (connection instanceof JdbcConnection) {
            Connection jdbcCon = ((JdbcConnection) connection).getWrappedConnection();
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

                // ConnectionImpl stores the properties, and the jdbc connection is a subclass of it...
                Field propsField = connectionImplClass.getDeclaredField("props");
                propsField.setAccessible(true);
                Properties props = (Properties) propsField.get(jdbcCon);
                String password = props.getProperty("password");
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
        return null;
    }
}

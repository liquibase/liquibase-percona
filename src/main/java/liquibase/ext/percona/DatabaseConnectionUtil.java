package liquibase.ext.percona;

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

    public String getPassword() {
        if (connection instanceof JdbcConnection) {
            Connection jdbcCon = ((JdbcConnection) connection).getWrappedConnection();
            try {
                // for MySQL, jdbcCon would be JDBC4Connection and superclass
                // would be ConnectionImpl
                Field propsField = jdbcCon.getClass().getSuperclass().getDeclaredField("props");
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

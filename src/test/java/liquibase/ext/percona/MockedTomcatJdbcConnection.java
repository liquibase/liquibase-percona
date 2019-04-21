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

import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DisposableConnectionFacade;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.ProxyConnection;

import com.mysql.jdbc.ConnectionImpl;

public class MockedTomcatJdbcConnection {

    /**
     * Creates a mocked SQL connection, that looks like a tomcat-jdbc pooled connection.
     * @param username the username to use
     * @param password the password to use
     * @return the connection
     * @throws SQLException
     */
    public static Connection create(String username, String password) throws SQLException {
        PoolProperties poolProps = new PoolProperties();
        poolProps.setUsername(username);
        poolProps.setPassword(password);
        poolProps.setDataSource(new MockDataSource());
        ConnectionPool pool = new ConnectionPool(poolProps);
        PooledConnection pooledConnection = new PooledConnection(poolProps, pool);
        pooledConnection.connect();
        ProxyConnection proxyConnection = new ProxyConnection(null, pooledConnection, true) {};
        DisposableConnectionFacade invocationHandler = new DisposableConnectionFacade(proxyConnection) {};
        Connection connection = (Connection) Proxy.newProxyInstance(DisposableConnectionFacade.class.getClassLoader(), new Class[] {Connection.class}, invocationHandler);
        return connection;
    }

    private static class MockDataSource implements DataSource {
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }
        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException();
        }
        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException();
        }
        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new UnsupportedOperationException();
        }
        @Override
        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException();
        }
        @Override
        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException();
        }
        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            Properties info = new Properties();
            info.setProperty("user", username);
            info.setProperty("password", password);
            return new NoOpMySqlConnection("host", 3306, info, "database", "jdbc:mysql://");
        }
        @Override
        public Connection getConnection() throws SQLException {
            Properties info = new Properties();
            return new NoOpMySqlConnection("host", 3306, info, "database", "jdbc:mysql://");
        }
    }

    private static class NoOpMySqlConnection extends ConnectionImpl {
        private static final long serialVersionUID = 6542644310976667501L;
        public NoOpMySqlConnection(String hostToConnectTo, int portToConnectTo, Properties info,
                String databaseToConnectTo, String url) throws SQLException {
            super(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
        }
        @Override
        public void createNewIO(boolean isForReconnect) throws SQLException {
            // overridden, so that this connection doesn't really try to connect
        }
    }
}

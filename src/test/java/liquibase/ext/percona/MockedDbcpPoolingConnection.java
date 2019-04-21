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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.BaseObjectPool;
import org.apache.commons.pool.ObjectPool;

import com.mysql.jdbc.ConnectionImpl;

public class MockedDbcpPoolingConnection {

    /**
     * Creates a mocked SQL connection, that looks like a apache commons dbcp pooled connection.
     * @param username the username to use
     * @param password the password to use
     * @return the connection
     * @throws SQLException
     */
    public static Connection create(String username, String password) throws SQLException {
        ObjectPool pool = new MockedObjectPool(username, password);
        PoolingDataSource ds = new PoolingDataSource(pool);
        return ds.getConnection();
    }

    private static class MockedObjectPool extends BaseObjectPool {
        private final String username;
        private final String password;

        public MockedObjectPool(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public Object borrowObject() throws Exception {
            Properties info = new Properties();
            info.setProperty("user", username);
            info.setProperty("password", password);
            return new NoOpMySqlConnection("host", 1234, info, "database", "jdbc:mysql://");
        }

        @Override
        public void returnObject(Object obj) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidateObject(Object obj) throws Exception {
            throw new UnsupportedOperationException();
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

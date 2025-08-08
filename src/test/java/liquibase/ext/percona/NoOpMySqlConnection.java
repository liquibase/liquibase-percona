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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.cj.NativeSession;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcPropertySetImpl;
import com.mysql.cj.jdbc.MockDatabaseMetaData;
import com.mysql.cj.protocol.NetworkResources;

public class NoOpMySqlConnection extends ConnectionImpl {
    private static final long serialVersionUID = 6542644310976667501L;

    private final Properties info;

    public NoOpMySqlConnection(String hostToConnectTo, int portToConnectTo, Properties info,
            String databaseToConnectTo, String url) throws SQLException {
        super();
        this.info = info;

        HostInfo hostInfo = convert(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
        this.props = hostInfo.exposeAsProperties();
        this.propertySet = new JdbcPropertySetImpl();
        this.propertySet.initializeProperties(this.props);
    }

    private static HostInfo convert(String hostToConnectTo, int portToConnectTo, Properties info,
            String databaseToConnectTo, String url) {
        ConnectionUrlParser con = ConnectionUrlParser.parseConnectionString(url);
        String user = info.getProperty(PropertyKey.USER.getKeyName());
        String password = info.getProperty(PropertyKey.PASSWORD.getKeyName());
        HostInfo hostInfo = new HostInfo(con, hostToConnectTo, portToConnectTo, user, password);
        return hostInfo;
    }

    @Override
    public void createNewIO(boolean isForReconnect) {
        // overridden, so that this connection doesn't really try to connect
    }

    @Override
    public void unSafeQueryInterceptors() throws SQLException {
        // overridden, so that this connection doesn't really try to connect
    }

    @Override
    public NativeSession getSession() {
        return new NativeSession(null, new DefaultPropertySet()) {
            private static final long serialVersionUID = -9008867607585078150L;

            @Override
            public NetworkResources getNetworkResources() {
                // overridden, so that this connection doesn't really try to connect
                return null;
            }
        };
    }

    @Override
    public boolean storesLowerCaseTableName() {
        // overridden, so that this connection doesn't really try to connect
        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        final String user = info.getProperty(PropertyKey.USER.getKeyName());
        return new MockDatabaseMetaData(this, user);
    }
}

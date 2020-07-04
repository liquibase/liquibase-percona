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

import java.sql.Driver;
import java.util.Properties;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;

public class MockDatabaseConnection implements DatabaseConnection {
    private String url;
    private String user;

    public MockDatabaseConnection(String url, String user) {
        super();
        this.url = url;
        this.user = user;
    }

    public static MockDatabaseConnection fromUrl(String url) {
        return new MockDatabaseConnection(url, "user@localhost");
    }

    public static MockDatabaseConnection fromUser(String user) {
        return new MockDatabaseConnection("jdbc:mysql://user@localhost:3306/testdb", user);
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getConnectionUserName() {
        return user;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws DatabaseException {
    }

    @Override
    public void rollback() throws DatabaseException {
    }

    @Override
    public String nativeSQL(String sql) throws DatabaseException {
        return null;
    }

    @Override
    public boolean isClosed() throws DatabaseException {
        return false;
    }

    @Override
    public String getDatabaseProductVersion() throws DatabaseException {
        return null;
    }

    @Override
    public String getDatabaseProductName() throws DatabaseException {
        return null;
    }

    @Override
    public int getDatabaseMinorVersion() throws DatabaseException {
        return 0;
    }

    @Override
    public int getDatabaseMajorVersion() throws DatabaseException {
        return 0;
    }

    @Override
    public String getCatalog() throws DatabaseException {
        return null;
    }

    public boolean getAutoCommit() throws DatabaseException {
        return false;
    }

    @Override
    public void commit() throws DatabaseException {
    }

    @Override
    public void close() throws DatabaseException {
    }

    @Override
    public void attached(Database database) {
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void open(String url, Driver driverObject, Properties driverProperties) throws DatabaseException {
    }
}

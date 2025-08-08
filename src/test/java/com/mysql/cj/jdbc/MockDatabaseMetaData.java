package com.mysql.cj.jdbc;

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

import java.sql.SQLException;

import liquibase.ext.percona.NoOpMySqlConnection;

public class MockDatabaseMetaData extends DatabaseMetaDataMysqlSchema {
    private final String user;

    public MockDatabaseMetaData(NoOpMySqlConnection connection, String user) {
        super(connection, "testdb", null);
        this.user = user;
    }

    @Override
    public String getURL() throws SQLException {
        return "jdbc:mysql://user@localhost:3306/testdb";
    }

    @Override
    public String getUserName() throws SQLException {
        return user;
    }
}

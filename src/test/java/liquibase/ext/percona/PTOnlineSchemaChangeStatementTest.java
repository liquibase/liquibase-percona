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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;

public class PTOnlineSchemaChangeStatementTest {
    private Database database;

    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Before
    public void setup() {
        System.setProperty(Configuration.LIQUIBASE_PASSWORD, "root");

        database = new MySQLDatabase();
        database.setLiquibaseSchemaName("testdb");
        DatabaseConnection conn = new MockDatabaseConnection("jdbc:mysql://user@localhost:3306/testdb",
                "user@localhost");
        database.setConnection(conn);
    }

    @Test
    public void checkPerconaToolkitIsAvailable() {
        PTOnlineSchemaChangeStatement.available = null;
        if (PTOnlineSchemaChangeStatement.isAvailable()) {
            System.out.println("pt-online-schema-change is available.");
        } else {
            System.out.println("pt-online-schema-change is NOT available.");
        }
        System.out.println("Version is " + PTOnlineSchemaChangeStatement.getVersion());
    }

    @Test
    public void testBuildCommand() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL");
        Assert.assertEquals(
                "[pt-online-schema-change, --alter=ADD COLUMN new_column INT NULL, --alter-foreign-keys-method=auto, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testBuildCommand2() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL");
        Assert.assertEquals(
                "[pt-online-schema-change, --alter=ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL, --alter-foreign-keys-method=auto, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testPrintCommand() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL");
        Assert.assertEquals(
                "pt-online-schema-change --alter=\"ADD COLUMN new_column INT NULL\" --alter-foreign-keys-method=auto --host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                statement.printCommand(database));
    }

    @Test
    public void testAdditionalOptions() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config /tmp/percona.conf");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL");
        Assert.assertEquals(
                "[pt-online-schema-change, --config, /tmp/percona.conf, --alter=ADD COLUMN new_column INT NULL, --alter-foreign-keys-method=auto, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testMultipleAdditionalOptions() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config /tmp/percona.conf --alter-foreign-keys-method=auto");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL");
        Assert.assertEquals(
                "[pt-online-schema-change, --config, /tmp/percona.conf, --alter-foreign-keys-method=auto, --alter=ADD COLUMN new_column INT NULL, --alter-foreign-keys-method=auto, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testAdditionalOptionsWithSpaces() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config \"/tmp/file with spaces.conf\"");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL");
        Assert.assertEquals(
                "[pt-online-schema-change, --config, /tmp/file with spaces.conf, --alter=ADD COLUMN new_column INT NULL, --alter-foreign-keys-method=auto, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testAdditionalOptionsWithQuotes() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config \"/tmp/percona.conf\"");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL");
        Assert.assertEquals(
                "[pt-online-schema-change, --config, /tmp/percona.conf, --alter=ADD COLUMN new_column INT NULL, --alter-foreign-keys-method=auto, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }
}

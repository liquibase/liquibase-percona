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

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;

@ExtendWith(RestoreSystemPropertiesExtension.class)
public class PTOnlineSchemaChangeStatementTest {
    private Database database;

    @BeforeEach
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
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --alter-foreign-keys-method=auto, --nocheck-unique-key-change, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testBuildCommandWithPerconaOptions() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.of("--per-change-option"));
        Assertions.assertEquals(
                "[pt-online-schema-change, --per-change-option, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testBuildCommand2() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --alter-foreign-keys-method=auto, --nocheck-unique-key-change, --alter=ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testPrintCommand() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"ADD COLUMN new_column INT NULL\" --password=*** --execute h=localhost,P=3306,u=user,D=testdb,t=person",
                statement.printCommand(database));
    }

    @Test
    public void testPrintCommandWithAdditionalOptions() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--slave-password=password");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "pt-online-schema-change --slave-password=*** --alter=\"ADD COLUMN new_column INT NULL\" --password=*** --execute h=localhost,P=3306,u=user,D=testdb,t=person",
                statement.printCommand(database));
    }

    @Test
    public void testAdditionalOptions() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config /tmp/percona.conf");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --config, /tmp/percona.conf, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testMultipleAdditionalOptions() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config /tmp/percona.conf --alter-foreign-keys-method=auto");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --config, /tmp/percona.conf, --alter-foreign-keys-method=auto, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testAdditionalOptionsWithSpaces() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config \"/tmp/file with spaces.conf\"");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --config, /tmp/file with spaces.conf, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testAdditionalOptionsWithQuotes() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--config \"/tmp/percona.conf\"");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --config, /tmp/percona.conf, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testAdditionalOptionsMultipleWithQuotes() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--critical-load=\"Threads_running=160\" --alter-foreign-keys-method=\"auto\"");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --critical-load=Threads_running=160, --alter-foreign-keys-method=auto, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testAdditionalOptionsMultipleWithQuotesAndSpaces() {
        System.setProperty(Configuration.ADDITIONAL_OPTIONS, "--arg1=\"val1 val2\" --alter-foreign-keys-method=\"auto\"");
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("testdb", "person",
                "ADD COLUMN new_column INT NULL", Optional.empty());
        Assertions.assertEquals(
                "[pt-online-schema-change, --arg1=val1 val2, --alter-foreign-keys-method=auto, --alter=ADD COLUMN new_column INT NULL, --password=root, --execute, h=localhost,P=3306,u=user,D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }
}

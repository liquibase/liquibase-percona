package liquibase.ext.percona;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PTOnlineSchemaChangeStatementTest {
    private Database database;

    @Before
    public void setup() {
        DatabaseConnectionUtil.passwordForTests = "root";

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
                "[pt-online-schema-change, --alter=ADD COLUMN new_column INT NULL, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testBuildCommand2() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL");
        Assert.assertEquals(
                "[pt-online-schema-change, --alter=ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL, --host=localhost, --port=3306, --user=user, --password=root, --execute, D=testdb,t=person]",
                String.valueOf(statement.buildCommand(database)));
    }

    @Test
    public void testPrintCommand() {
        PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement("person",
                "ADD COLUMN new_column INT NULL");
        Assert.assertEquals(
                "pt-online-schema-change --alter=\"ADD COLUMN new_column INT NULL\" --host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                statement.printCommand(database));
    }
}

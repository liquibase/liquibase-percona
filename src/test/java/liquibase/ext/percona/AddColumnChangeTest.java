package liquibase.ext.percona;

import java.io.StringWriter;

import liquibase.change.AddColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.executor.jvm.JdbcExecutor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.CommentStatement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddColumnChangeTest {

    private AddColumnChange c;
    private Database database;

    @Before
    public void setup() {
        c = new AddColumnChange();
        AddColumnConfig column = new AddColumnConfig();
        column.setName("new_column");
        column.setType("INT");
        c.addColumn(column);
        c.setTableName("person");
        DatabaseConnectionUtil.passwordForTests = "root";

        database = new MySQLDatabase();
        database.setLiquibaseSchemaName("testdb");
        DatabaseConnection conn = new MockDatabaseConnection("jdbc:mysql://user@localhost:3306/testdb",
                "user@localhost");
        database.setConnection(conn);
        ExecutorService.getInstance().setExecutor(database, new JdbcExecutor());

        PTOnlineSchemaChangeStatement.available = true;
    }

    @Test
    public void testWithoutPercona() {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(AddColumnStatement.class, statements[0].getClass());
    }

    @Test
    public void testReal() {
        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(PTOnlineSchemaChangeStatement.class, statements[0].getClass());
    }

    @Test
    public void testUpdateSQL() {
        ExecutorService.getInstance().setExecutor(database, new LoggingExecutor(null, new StringWriter(), database));

        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(AddColumnStatement.class, statements[2].getClass());
    }

    @Test
    public void testGenerateAlterMultipleColumns() {
        AddColumnConfig column = new AddColumnConfig();
        column.setName("email");
        column.setType("varchar(255)");
        c.addColumn(column);

        Assert.assertEquals("ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL",
                c.generateAlterStatement(database));
    }

    @Test
    public void testConvertColumnToSql() {
        Assert.assertEquals("ADD COLUMN new_column INT NULL", c.convertToSql(c.getColumns().get(0), database));

        AddColumnConfig column = new AddColumnConfig();
        column.setName("email");
        column.setType("varchar(255)");
        ConstraintsConfig constraints = new ConstraintsConfig();
        constraints.setNullable(false);
        column.setConstraints(constraints);
        Assert.assertEquals("ADD COLUMN email VARCHAR(255) NOT NULL", c.convertToSql(column, database));

        column.setDefaultValue("no-email@example.org");
        Assert.assertEquals("ADD COLUMN email VARCHAR(255) NOT NULL DEFAULT 'no-email@example.org'",
                c.convertToSql(column, database));

        column.setRemarks("that is the email");
        Assert.assertEquals(
                "ADD COLUMN email VARCHAR(255) NOT NULL DEFAULT 'no-email@example.org' COMMENT 'that is the email'",
                c.convertToSql(column, database));
    }
}

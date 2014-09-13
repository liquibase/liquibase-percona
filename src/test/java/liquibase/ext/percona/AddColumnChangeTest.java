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
import java.io.StringWriter;

import liquibase.change.AddColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;
import liquibase.exception.RollbackImpossibleException;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.executor.jvm.JdbcExecutor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.DropColumnStatement;

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
        System.setProperty(Configuration.FAIL_IF_NO_PT, "false");
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "false");
    }

    @Test
    public void testWithoutPercona() {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(AddColumnStatement.class, statements[0].getClass());
    }

    @Test
    public void testWithoutPerconaRollback() throws RollbackImpossibleException {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = c.generateRollbackStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(DropColumnStatement.class, statements[0].getClass());
    }

    @Test(expected = RuntimeException.class)
    public void testWithoutPerconaAndFail() {
        System.setProperty(Configuration.FAIL_IF_NO_PT, "true");
        PTOnlineSchemaChangeStatement.available = false;

        c.generateStatements(database);
    }

    @Test(expected = RuntimeException.class)
    public void testWithoutPerdatabaseconaRollbackAndFail() throws RollbackImpossibleException {
        System.setProperty(Configuration.FAIL_IF_NO_PT, "true");
        PTOnlineSchemaChangeStatement.available = false;

        c.generateRollbackStatements(database);
    }

    @Test
    public void testReal() {
        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(PTOnlineSchemaChangeStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"ADD COLUMN new_column INT NULL\" "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((PTOnlineSchemaChangeStatement)statements[0]).printCommand(database));
    }

    @Test
    public void testRealRollback() throws RollbackImpossibleException {
        SqlStatement[] statements = c.generateRollbackStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(PTOnlineSchemaChangeStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"DROP COLUMN new_column\" "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((PTOnlineSchemaChangeStatement)statements[0]).printCommand(database));
    }

    @Test
    public void testUpdateSQL() {
        ExecutorService.getInstance().setExecutor(database, new LoggingExecutor(null, new StringWriter(), database));

        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"ADD COLUMN new_column INT NULL\" "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(AddColumnStatement.class, statements[2].getClass());
    }

    @Test
    public void testRollbackSQL() throws RollbackImpossibleException {
        ExecutorService.getInstance().setExecutor(database, new LoggingExecutor(null, new StringWriter(), database));

        SqlStatement[] statements = c.generateRollbackStatements(database);
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"DROP COLUMN new_column\" "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(DropColumnStatement.class, statements[2].getClass());
    }

    @Test
    public void testUpdateSQLNoAlterSqlDryMode() {
        ExecutorService.getInstance().setExecutor(database, new LoggingExecutor(null, new StringWriter(), database));
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"ADD COLUMN new_column INT NULL\" "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
    }

    @Test
    public void testRollbackSQLNoAlterSqlDryMode() throws RollbackImpossibleException {
        ExecutorService.getInstance().setExecutor(database, new LoggingExecutor(null, new StringWriter(), database));
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = c.generateRollbackStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"DROP COLUMN new_column\" "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
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
        Assert.assertEquals("ADD COLUMN new_column INT NULL", c.convertColumnToSql(c.getColumns().get(0), database));

        AddColumnConfig column = new AddColumnConfig();
        column.setName("email");
        column.setType("varchar(255)");
        ConstraintsConfig constraints = new ConstraintsConfig();
        constraints.setNullable(false);
        column.setConstraints(constraints);
        Assert.assertEquals("ADD COLUMN email VARCHAR(255) NOT NULL", c.convertColumnToSql(column, database));

        column.setDefaultValue("no-email@example.org");
        Assert.assertEquals("ADD COLUMN email VARCHAR(255) NOT NULL DEFAULT 'no-email@example.org'",
                c.convertColumnToSql(column, database));

        column.setRemarks("that is the email");
        Assert.assertEquals(
                "ADD COLUMN email VARCHAR(255) NOT NULL DEFAULT 'no-email@example.org' COMMENT 'that is the email'",
                c.convertColumnToSql(column, database));
    }
}

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import liquibase.change.AddColumnConfig;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.executor.jvm.JdbcExecutor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.CreateIndexStatement;

public class PerconaCreateIndexChangeTest
{

    private PerconaCreateIndexChange c;
    private Database database;

    @Before
    public void setup() {
        c = new PerconaCreateIndexChange();
        AddColumnConfig column = new AddColumnConfig();
        column.setName( "indexedColumn" );
        c.addColumn( column );
        c.setTableName( "person" );
        c.setIndexName( "theIndexName" );
        c.setUnique( true );

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

    private void assertPerconaChange(SqlStatement[] statements, String alter) {
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(PTOnlineSchemaChangeStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"" + alter + "\" "
                + "--alter-foreign-keys-method=auto "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((PTOnlineSchemaChangeStatement)statements[0]).printCommand(database));
    }

    @Test
    public void testCreateNewIndexReal() {
        SqlStatement[] statements = c.generateStatements(database);
        assertPerconaChange(statements, "ADD UNIQUE INDEX theIndexName (indexedColumn)");
    }

    @Test
    public void testCreateIndexNonUnique() {
        c.setUnique(false);
        SqlStatement[] statements = c.generateStatements(database);
        assertPerconaChange(statements, "ADD INDEX theIndexName (indexedColumn)");
    }

    @Test
    public void testCreateIndexMultipleColumns() {
        AddColumnConfig column2 = new AddColumnConfig();
        column2.setName("otherColumn");
        c.addColumn(column2);
        SqlStatement[] statements = c.generateStatements(database);
        assertPerconaChange(statements, "ADD UNIQUE INDEX theIndexName (indexedColumn, otherColumn)");
    }

    @Test
    public void testCreateIndexColumnWithType() {
        AddColumnConfig column = new AddColumnConfig();
        column.setName("otherIntColumn");
        column.setType("INT");
        c.getColumns().clear();
        c.addColumn(column);
        SqlStatement[] statements = c.generateStatements(database);
        assertPerconaChange(statements, "ADD UNIQUE INDEX theIndexName (otherIntColumn)");
    }

    @Test
    public void testUpdateSQL() {
        ExecutorService.getInstance().setExecutor(database, new LoggingExecutor(null, new StringWriter(), database));

        SqlStatement[] statements = c.generateStatements(database);
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"ADD UNIQUE INDEX theIndexName (indexedColumn)\" "
                + "--alter-foreign-keys-method=auto "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(CreateIndexStatement.class, statements[2].getClass());
    }
    }
}

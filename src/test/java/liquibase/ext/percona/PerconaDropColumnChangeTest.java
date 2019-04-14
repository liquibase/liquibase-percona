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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import liquibase.change.ColumnConfig;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.DropColumnStatement;

public class PerconaDropColumnChangeTest extends AbstractPerconaChangeTest<PerconaDropColumnChange> {

    public PerconaDropColumnChangeTest() {
        super(PerconaDropColumnChange.class);
    }

    @Override
    protected void setupChange(PerconaDropColumnChange change) {
        change.setColumnName("col_test");
        change.setTableName("person");
        change.getColumns().clear();
    }

    @Test
    public void testGenerateAlterStatement() {
        Assertions.assertEquals("DROP COLUMN col_test", getChange().generateAlterStatement(getDatabase()));
    }

    @Test
    public void testGenerateAlterStatementMultipleColumns() {
        ColumnConfig col1 = new ColumnConfig();
        col1.setName("col1_test");
        getChange().addColumn(col1);
        ColumnConfig col2 = new ColumnConfig();
        col2.setName("col2_test");
        getChange().addColumn(col2);

        Assertions.assertEquals("DROP COLUMN col1_test, DROP COLUMN col2_test", getChange().generateAlterStatement(getDatabase()));
    }

    @Test
    public void testWithoutPercona() {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateStatements();
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(DropColumnStatement.class, statements[0].getClass());
    }

    @Test
    public void testWithoutPerconaAndFail() {
        System.setProperty(Configuration.FAIL_IF_NO_PT, "true");
        PTOnlineSchemaChangeStatement.available = false;

        Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                generateStatements();
            }
        });
    }

    @Test
    public void testReal() {
        assertPerconaChange("DROP COLUMN col_test");
    }

    @Test
    public void testUpdateSQL() {
        enableLogging();

        SqlStatement[] statements = generateStatements();
        Assertions.assertEquals(3, statements.length);
        Assertions.assertEquals(CommentStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change --alter=\"DROP COLUMN col_test\" "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assertions.assertEquals(CommentStatement.class, statements[1].getClass());
        Assertions.assertEquals(DropColumnStatement.class, statements[2].getClass());
    }

    @Test
    public void testUpdateSQLNoAlterSqlDryMode() {
        enableLogging();
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = generateStatements();
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(CommentStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change --alter=\"DROP COLUMN col_test\" "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
    }
}

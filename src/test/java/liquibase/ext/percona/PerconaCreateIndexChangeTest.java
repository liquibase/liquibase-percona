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

import liquibase.change.AddColumnConfig;
import liquibase.exception.RollbackImpossibleException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.CreateIndexStatement;

public class PerconaCreateIndexChangeTest extends AbstractPerconaChangeTest<PerconaCreateIndexChange> {

    public PerconaCreateIndexChangeTest() {
        super(PerconaCreateIndexChange.class);
    }

    @Override
    protected void setupChange(PerconaCreateIndexChange change) {
        AddColumnConfig column = new AddColumnConfig();
        column.setName( "indexedColumn" );
        change.addColumn( column );
        change.setTableName( "person" );
        change.setIndexName( "theIndexName" );
        change.setUnique( true );

        alterText = "ADD UNIQUE INDEX theIndexName (indexedColumn)";
    }

    @Test
    public void testCreateNewIndexReal() {
        assertPerconaChange(alterText);
    }

    @Test
    public void testCreateNewIndexRealWithPrefix() {
        getChange().getColumns().get(0).setName( "indexedColumn(10)" );
        assertPerconaChange( "ADD UNIQUE INDEX theIndexName (indexedColumn(10))" );
    }

    @Test
    public void testCreateIndexNonUnique() {
        getChange().setUnique(false);
        assertPerconaChange("ADD INDEX theIndexName (indexedColumn)");
    }

    @Test
    public void testCreateIndexMultipleColumns() {
        AddColumnConfig column2 = new AddColumnConfig();
        column2.setName("otherColumn");
        getChange().addColumn(column2);
        assertPerconaChange("ADD UNIQUE INDEX theIndexName (indexedColumn, otherColumn)");
    }

    @Test
    public void testCreateIndexColumnWithType() {
        AddColumnConfig column = new AddColumnConfig();
        column.setName("otherIntColumn");
        column.setType("INT");
        getChange().getColumns().clear();
        getChange().addColumn(column);
        assertPerconaChange("ADD UNIQUE INDEX theIndexName (otherIntColumn)");
    }

    @Test
    public void testUpdateSQL() {
        enableLogging();

        SqlStatement[] statements = generateStatements();
        Assertions.assertEquals(3, statements.length);
        Assertions.assertEquals(CommentStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--alter=\"ADD UNIQUE INDEX theIndexName (indexedColumn)\" "
                + "--password=*** --execute "
                + "h=localhost,P=3306,u=user,D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assertions.assertEquals(CommentStatement.class, statements[1].getClass());
        Assertions.assertEquals(CreateIndexStatement.class, statements[2].getClass());
    }

    @Test
    public void testRollback() throws RollbackImpossibleException {
        assertPerconaRollbackChange("DROP INDEX theIndexName");
    }

    @Test
    public void testWithComputedColumn() {
        AddColumnConfig column = new AddColumnConfig();
        column.setName("computedName", true);
        AddColumnConfig column2 = new AddColumnConfig();
        column2.setName("computed2", false);
        getChange().getColumns().clear();
        getChange().addColumn(column);
        getChange().addColumn(column2);
        assertPerconaChange("ADD UNIQUE INDEX theIndexName (computedName, computed2)");
    }
}

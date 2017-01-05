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
import org.junit.Test;

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
    }

    @Test
    public void testCreateNewIndexReal() {
        assertPerconaChange("ADD UNIQUE INDEX theIndexName (indexedColumn)");
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
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"ADD UNIQUE INDEX theIndexName (indexedColumn)\" "
                + "--alter-foreign-keys-method=auto "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
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

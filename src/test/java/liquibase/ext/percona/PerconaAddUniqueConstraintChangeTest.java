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

import liquibase.exception.RollbackImpossibleException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.AddUniqueConstraintStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.DropUniqueConstraintStatement;

public class PerconaAddUniqueConstraintChangeTest extends AbstractPerconaChangeTest<PerconaAddUniqueConstraintChange> {

    public PerconaAddUniqueConstraintChangeTest() {
        super(PerconaAddUniqueConstraintChange.class);
    }

    private String alterText;
    private String alterRollbackText;

    @Override
    protected void setupChange(PerconaAddUniqueConstraintChange change) {
        change.setTableName("person");
        change.setColumnNames("id, name");
        change.setConstraintName("uq_id_name");
        change.setDeferrable(true);
        change.setInitiallyDeferred(true);

        alterText = "ADD CONSTRAINT uq_id_name UNIQUE (id, name)";
        alterRollbackText = "DROP KEY uq_id_name";
    }

    @Test
    public void testWithoutPercona() {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(AddUniqueConstraintStatement.class, statements[0].getClass());
    }

    @Test
    public void testWithoutPerconaRollback() throws RollbackImpossibleException {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateRollbackStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(DropUniqueConstraintStatement.class, statements[0].getClass());
    }

    @Test(expected = RuntimeException.class)
    public void testWithoutPerconaAndFail() {
        System.setProperty(Configuration.FAIL_IF_NO_PT, "true");
        PTOnlineSchemaChangeStatement.available = false;

        generateStatements();
    }

    @Test(expected = RuntimeException.class)
    public void testWithoutPerdatabaseconaRollbackAndFail() throws RollbackImpossibleException {
        System.setProperty(Configuration.FAIL_IF_NO_PT, "true");
        PTOnlineSchemaChangeStatement.available = false;

        generateRollbackStatements();
    }

    @Test
    public void testReal() {
        assertPerconaChange(alterText);
    }

    @Test
    public void testRealRollback() throws RollbackImpossibleException {
        assertPerconaRollbackChange(alterRollbackText);
    }

    @Test
    public void testUpdateSQL() {
        enableLogging();

        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"" + alterText + "\" "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(AddUniqueConstraintStatement.class, statements[2].getClass());
    }

    @Test
    public void testRollbackSQL() throws RollbackImpossibleException {
        enableLogging();

        SqlStatement[] statements = generateRollbackStatements();
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"" + alterRollbackText + "\" "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(DropUniqueConstraintStatement.class, statements[2].getClass());
    }

    @Test
    public void testUpdateSQLNoAlterSqlDryMode() {
        enableLogging();
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"" + alterText + "\" "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
    }

    @Test
    public void testRollbackSQLNoAlterSqlDryMode() throws RollbackImpossibleException {
        enableLogging();
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = generateRollbackStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"" + alterRollbackText + "\" "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
    }

    @Test
    public void testSkipAddUniqueConstraintChange() {
        System.setProperty(Configuration.SKIP_CHANGES, "addUniqueConstraint");
        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(AddUniqueConstraintStatement.class, statements[0].getClass());
    }
}

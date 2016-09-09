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
import liquibase.change.ConstraintsConfig;
import liquibase.database.Database;
import liquibase.exception.RollbackImpossibleException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.DropColumnStatement;

public class PerconaAddColumnChangeTest extends AbstractPerconaChangeTest<PerconaAddColumnChange> {

    public PerconaAddColumnChangeTest() {
        super(PerconaAddColumnChange.class);
    }

    @Override
    protected void setupChange(PerconaAddColumnChange change) {
        AddColumnConfig column = new AddColumnConfig();
        column.setName("new_column");
        column.setType("INT");
        change.addColumn(column);
        change.setTableName("person");
    }

    @Test
    public void testWithoutPercona() {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(AddColumnStatement.class, statements[0].getClass());
    }

    @Test
    public void testWithoutPerconaRollback() throws RollbackImpossibleException {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateRollbackStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(DropColumnStatement.class, statements[0].getClass());
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
        assertPerconaChange("ADD COLUMN new_column INT NULL");
    }

    @Test
    public void testRealRollback() throws RollbackImpossibleException {
        assertPerconaRollbackChange("DROP COLUMN new_column");
    }

    @Test
    public void testUpdateSQL() {
        enableLogging();

        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"ADD COLUMN new_column INT NULL\" "
                + "--alter-foreign-keys-method=auto "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(AddColumnStatement.class, statements[2].getClass());
    }

    @Test
    public void testRollbackSQL() throws RollbackImpossibleException {
        enableLogging();

        SqlStatement[] statements = generateRollbackStatements();
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"DROP COLUMN new_column\" "
                + "--alter-foreign-keys-method=auto "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        Assert.assertEquals(CommentStatement.class, statements[1].getClass());
        Assert.assertEquals(DropColumnStatement.class, statements[2].getClass());
    }

    @Test
    public void testUpdateSQLNoAlterSqlDryMode() {
        enableLogging();
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(CommentStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"ADD COLUMN new_column INT NULL\" "
                + "--alter-foreign-keys-method=auto "
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
        Assert.assertEquals("pt-online-schema-change --alter=\"DROP COLUMN new_column\" "
                + "--alter-foreign-keys-method=auto "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
    }

    @Test
    public void testGenerateAlterMultipleColumns() {
        AddColumnConfig column = new AddColumnConfig();
        column.setName("email");
        column.setType("varchar(255)");
        getChange().addColumn(column);

        Assert.assertEquals("ADD COLUMN new_column INT NULL, ADD COLUMN email VARCHAR(255) NULL",
                getChange().generateAlterStatement(getDatabase()));
    }

    @Test
    public void testConvertColumnToSql() {
        PerconaAddColumnChange c = getChange();
        Database database = getDatabase();
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

    @Test
    public void testSkipAddColumnChange() {
        System.setProperty(Configuration.SKIP_CHANGES, "addColumn");
        SqlStatement[] statements = generateStatements();
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(AddColumnStatement.class, statements[0].getClass());
    }

    @Test
    public void testConvertColumnToSqlWithAfter() {
        PerconaAddColumnChange c = getChange();
        Database database = getDatabase();

        AddColumnConfig column = new AddColumnConfig();
        column.setName("new_column");
        column.setType("INT");
        column.setAfterColumn("other_column");

        Assert.assertEquals("ADD COLUMN new_column INT NULL AFTER other_column",
                c.convertColumnToSql(column, database));

        column.setAfterColumn("other column");
        Assert.assertEquals("ADD COLUMN new_column INT NULL AFTER `other column`",
                c.convertColumnToSql(column, database));

        column.setAfterColumn("");
        Assert.assertEquals("ADD COLUMN new_column INT NULL",
                c.convertColumnToSql(column, database));
    }
}

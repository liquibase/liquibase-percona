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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import liquibase.exception.ValidationErrors;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.RawSqlStatement;

public class PerconaRawSQLChangeTest extends AbstractPerconaChangeTest<PerconaRawSQLChange> {

    public PerconaRawSQLChangeTest() {
        super(PerconaRawSQLChange.class);
    }

    @Override
    protected void setupChange(PerconaRawSQLChange change) {
        alterText = "ADD COLUMN address VARCHAR(255) NULL";
        change.setSql("alter table person " + alterText);
    }

    @Test
    public void testGetTargetTableName() {
        PerconaRawSQLChange change = getChange();
        assertEquals("person", change.getTargetTableName());
    }

    @Test
    public void testGenerateAlterStatement() {
        PerconaRawSQLChange change = getChange();
        assertEquals(alterText, change.generateAlterStatement(getDatabase()));
    }

    @Test
    public void testTargetTableNameAndAlterStatementKeepCase() {
        PerconaRawSQLChange change = getChange();
        change.setSql("altEr tAble pErSoN " + alterText);
        assertEquals("pErSoN", change.getTargetTableName());
        assertEquals(alterText, change.generateAlterStatement(getDatabase()));
        SqlStatement[] statements = change.generateStatements(getDatabase());
        assertEquals(1, statements.length);
        assertEquals(PTOnlineSchemaChangeStatement.class, statements[0].getClass());
        PTOnlineSchemaChangeStatement statement = (PTOnlineSchemaChangeStatement) statements[0];
        assertEquals("pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"ADD COLUMN address VARCHAR(255) NULL\" --password=*** --execute h=localhost,P=3306,u=user,D=testdb,t=pErSoN",
                statement.printCommand(getDatabase()));
    }

    @Test
    public void testTargetTableNameAndAlterStatementWithSpaces() {
        PerconaRawSQLChange change = getChange();
        change.setSql("  altEr   tAble   person   " + alterText + "  ");
        assertEquals("person", change.getTargetTableName());
        assertEquals(alterText, change.generateAlterStatement(getDatabase()));
        assertPerconaChange(alterText);
    }

    @Test
    public void testTargetTableNameAndAlterStatementWithEscapes() {
        PerconaRawSQLChange change = getChange();
        String alterTextEscaped = "ADD COLUMN `address` VARCHAR(255) NULL";
        change.setSql("altEr tAble `person` " + alterTextEscaped);
        assertEquals("person", change.getTargetTableName());
        assertEquals(alterTextEscaped, change.generateAlterStatement(getDatabase()));
        assertPerconaChange(alterTextEscaped);
    }

    @Test
    void testTargetTableNameAndAlterStatementWithQuotedTableNameWithSpaces() {
        PerconaRawSQLChange change = getChange();
        String alterTextEscaped = "ADD COLUMN `address` VARCHAR(255) NULL";
        change.setSql("altEr tAble `my pErSoN table` " + alterTextEscaped);
        assertEquals("my pErSoN table", change.getTargetTableName());
        assertEquals(alterTextEscaped, change.generateAlterStatement(getDatabase()));
        assertPerconaChangeWithDatabaseTableNames(alterTextEscaped, "testdb", "my pErSoN table");
    }

    @Test
    public void testTargetTableNameAndAlterStatementForInsert() {
        PerconaRawSQLChange change = getChange();
        change.setSql("insert into person (name) values ('Bob')");
        assertNoPerconaToolkit();
    }

    @Test
    public void testTargetTableNameAndAlterStatementForMultipleStatements() {
        PerconaRawSQLChange change = getChange();
        change.setSplitStatements(true);
        change.setSql("alter table person " + alterText + "; alter table person " + alterText + ";");
        Assertions.assertNull(change.getTargetTableName());
        Assertions.assertNull(change.generateAlterStatement(getDatabase()));
        SqlStatement[] sqlStatements = change.generateStatements(getDatabase());
        assertEquals(2, sqlStatements.length);
        assertEquals(RawSqlStatement.class, sqlStatements[0].getClass());
        assertEquals(RawSqlStatement.class, sqlStatements[1].getClass());
    }

    @Test
    public void testTargetTableNameAndAlterStatementWithComment() {
        PerconaRawSQLChange change = getChange();
        change.setSql("-- multiline\nalter table person " + alterText + ";\n/* other\ncomment */");
        assertEquals("person", change.getTargetTableName());
        assertEquals(alterText, change.generateAlterStatement(getDatabase()));
        assertPerconaChange(alterText);
    }

    @Test
    public void testTargetTableNameAndAlterStatementForMultipleAlterOptionsSameTable() {
        PerconaRawSQLChange change = getChange();
        String alterMultipleOptions = "add address varchar(255) null, add age int null";
        change.setSql("alter table person " + alterMultipleOptions);
        assertEquals("person", change.getTargetTableName());
        assertEquals(alterMultipleOptions, change.generateAlterStatement(getDatabase()));
        assertPerconaChange(alterMultipleOptions);
    }

    @Test
    void targetDatabaseNameAndTargetTable() {
        PerconaRawSQLChange change = getChange();
        String alterOptions = "ADD COLUMN `test_column` DATETIME NULL DEFAULT NULL";
        change.setSql("ALTER TABLE `test_database`.`test_table`\n " + alterOptions);
        assertEquals("test_database", change.getTargetDatabaseName());
        assertEquals("test_table", change.getTargetTableName());
        assertEquals(alterOptions, change.generateAlterStatement(getDatabase()));
        assertPerconaChangeWithDatabaseTableNames(alterOptions, "test_database", "test_table");
    }

    @Test
    public void testTargetTableNameAndAlterStatementForRename() {
        PerconaRawSQLChange change = getChange();
        change.setSql("alter table person rename new_person");
        assertNoPerconaToolkit();
        change.setSql("alter table person rename to new_person");
        assertNoPerconaToolkit();
        change.setSql("alter table person rename as new_person");
        assertNoPerconaToolkit();

        change.setSql("alter table person rename column name to new_name");
        assertEquals("person", change.getTargetTableName());
        assertEquals("rename column name to new_name", change.generateAlterStatement(getDatabase()));
        assertPerconaChange("rename column name to new_name");
        change.setSql("alter table person rename index name to new_name");
        assertEquals("person", change.getTargetTableName());
        assertEquals("rename index name to new_name", change.generateAlterStatement(getDatabase()));
        assertPerconaChange("rename index name to new_name");
        change.setSql("alter table person rename key name to new_name");
        assertEquals("person", change.getTargetTableName());
        assertEquals("rename key name to new_name", change.generateAlterStatement(getDatabase()));
        assertPerconaChange("rename key name to new_name");
    }

    private void assertNoPerconaToolkit() {
        PerconaRawSQLChange change = getChange();
        Assertions.assertNull(change.getTargetTableName());
        Assertions.assertNull(change.generateAlterStatement(getDatabase()));
        SqlStatement[] statements = generateStatements();
        assertEquals(1, statements.length);
        assertEquals(RawSqlStatement.class, statements[0].getClass());
    }

    @Test
    public void testGetTargetDatabaseName() {
        PerconaRawSQLChange change = getChange();
        Assertions.assertNull(change.getTargetDatabaseName());
    }

    @Test
    public void testWithoutPercona() {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateStatements();
        assertEquals(1, statements.length);
        assertEquals(RawSqlStatement.class, statements[0].getClass());
    }

    @Test
    public void testWithoutPerconaAndFail() {
        System.setProperty(Configuration.FAIL_IF_NO_PT, "true");
        PTOnlineSchemaChangeStatement.available = false;

        ValidationErrors errors = validate();
        Assertions.assertTrue(errors.hasErrors());
        assertEquals("No percona toolkit found!", errors.getErrorMessages().get(0));
    }

    @Test
    public void testReal() {
        assertPerconaChange(alterText);
    }

    @Test
    public void testUpdateSQL() {
        enableLogging();

        SqlStatement[] statements = generateStatements();
        assertEquals(3, statements.length);
        assertEquals(CommentStatement.class, statements[0].getClass());
        assertEquals("pt-online-schema-change "
                        + "--alter-foreign-keys-method=auto "
                        + "--nocheck-unique-key-change "
                        + "--alter=\"" + alterText + "\" "
                        + "--password=*** --execute "
                        + "h=localhost,P=3306,u=user,D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
        assertEquals(CommentStatement.class, statements[1].getClass());
        assertEquals(RawSqlStatement.class, statements[2].getClass());
    }

    @Test
    public void testUpdateSQLWithNewLines() {
        enableLogging();

        PerconaRawSQLChange change = getChange();
        change.setSql("alter table person ADD COLUMN address\nVARCHAR(255) NULL");

        SqlStatement[] statements = generateStatements();
        assertEquals(4, statements.length);
        assertEquals(CommentStatement.class, statements[0].getClass());
        assertEquals("pt-online-schema-change "
                        + "--alter-foreign-keys-method=auto "
                        + "--nocheck-unique-key-change "
                        + "--alter=\"ADD COLUMN address",
                ((CommentStatement)statements[0]).getText());
        assertEquals("VARCHAR(255) NULL\" "
                        + "--password=*** --execute "
                        + "h=localhost,P=3306,u=user,D=testdb,t=person",
                ((CommentStatement)statements[1]).getText());
        assertEquals(CommentStatement.class, statements[2].getClass());
        assertEquals(RawSqlStatement.class, statements[3].getClass());
    }

    @Test
    public void testUpdateSQLNoAlterSqlDryMode() {
        enableLogging();
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = generateStatements();
        assertEquals(1, statements.length);
        assertEquals(CommentStatement.class, statements[0].getClass());
        assertEquals("pt-online-schema-change "
                        + "--alter-foreign-keys-method=auto "
                        + "--nocheck-unique-key-change "
                        + "--alter=\"" + alterText + "\" "
                        + "--password=*** --execute "
                        + "h=localhost,P=3306,u=user,D=testdb,t=person",
                ((CommentStatement)statements[0]).getText());
    }

    @Test
    public void testSkipRawSQLChange() {
        System.setProperty(Configuration.SKIP_CHANGES, "sql");
        SqlStatement[] statements = generateStatements();
        assertEquals(1, statements.length);
        assertEquals(RawSqlStatement.class, statements[0].getClass());
    }
}

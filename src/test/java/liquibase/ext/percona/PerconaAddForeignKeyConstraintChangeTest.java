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

import liquibase.exception.RollbackImpossibleException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.AddForeignKeyConstraintStatement;
import liquibase.statement.core.CommentStatement;
import liquibase.statement.core.DropForeignKeyConstraintStatement;
import liquibase.structure.core.ForeignKeyConstraintType;

public class PerconaAddForeignKeyConstraintChangeTest extends AbstractPerconaChangeTest<PerconaAddForeignKeyConstraintChange> {

    public PerconaAddForeignKeyConstraintChangeTest() {
        super(PerconaAddForeignKeyConstraintChange.class);
    }

    private String alterRollbackText;

    @Override
    protected void setupChange(PerconaAddForeignKeyConstraintChange change) {
        change.setBaseTableName("address");
        change.setBaseColumnNames("person_id");
        change.setConstraintName("fk_address_person");
        change.setDeferrable(true);
        change.setInitiallyDeferred(true);
        change.setOnDelete(ForeignKeyConstraintType.importedKeyCascade);
        change.setOnUpdate(ForeignKeyConstraintType.importedKeyRestrict);
        change.setReferencedColumnNames("id");
        change.setReferencedTableName("person");

        setTargetTableName("address");
        alterText = "ADD CONSTRAINT fk_address_person FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE ON UPDATE RESTRICT DEFERRABLE INITIALLY DEFERRED";
        alterRollbackText = "DROP FOREIGN KEY _fk_address_person";
    }

    @Test
    public void testWithoutPercona() {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateStatements();
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(AddForeignKeyConstraintStatement.class, statements[0].getClass());
    }

    @Test
    public void testWithoutPerconaRollback() throws RollbackImpossibleException {
        PTOnlineSchemaChangeStatement.available = false;
        SqlStatement[] statements = generateRollbackStatements();
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(DropForeignKeyConstraintStatement.class, statements[0].getClass());
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
    public void testWithoutPerdatabaseconaRollbackAndFail() throws RollbackImpossibleException {
        System.setProperty(Configuration.FAIL_IF_NO_PT, "true");
        PTOnlineSchemaChangeStatement.available = false;

        Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                generateRollbackStatements();
            }
        });
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
        Assertions.assertEquals(3, statements.length);
        Assertions.assertEquals(CommentStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--alter=\"" + alterText + "\" "
                + "--password=*** --execute h=localhost,P=3306,u=user,D=testdb,t=address",
                ((CommentStatement)statements[0]).getText());
        Assertions.assertEquals(CommentStatement.class, statements[1].getClass());
        Assertions.assertEquals(AddForeignKeyConstraintStatement.class, statements[2].getClass());
    }

    @Test
    public void testRollbackSQL() throws RollbackImpossibleException {
        enableLogging();

        SqlStatement[] statements = generateRollbackStatements();
        Assertions.assertEquals(3, statements.length);
        Assertions.assertEquals(CommentStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--alter=\"" + alterRollbackText + "\" "
                + "--password=*** --execute h=localhost,P=3306,u=user,D=testdb,t=address",
                ((CommentStatement)statements[0]).getText());
        Assertions.assertEquals(CommentStatement.class, statements[1].getClass());
        Assertions.assertEquals(DropForeignKeyConstraintStatement.class, statements[2].getClass());
    }

    @Test
    public void testUpdateSQLNoAlterSqlDryMode() {
        enableLogging();
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = generateStatements();
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(CommentStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--alter=\"" + alterText + "\" "
                + "--password=*** --execute h=localhost,P=3306,u=user,D=testdb,t=address",
                ((CommentStatement)statements[0]).getText());
    }

    @Test
    public void testRollbackSQLNoAlterSqlDryMode() throws RollbackImpossibleException {
        enableLogging();
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "true");

        SqlStatement[] statements = generateRollbackStatements();
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(CommentStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--alter=\"" + alterRollbackText + "\" "
                + "--password=*** --execute h=localhost,P=3306,u=user,D=testdb,t=address",
                ((CommentStatement)statements[0]).getText());
    }

    @Test
    public void testSkipAddForeignKeyConstraintChange() {
        System.setProperty(Configuration.SKIP_CHANGES, "addForeignKeyConstraint");
        SqlStatement[] statements = generateStatements();
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(AddForeignKeyConstraintStatement.class, statements[0].getClass());
    }

    @Test
    public void testSelfReferencingForeignKey() {
        PerconaAddForeignKeyConstraintChange change = new PerconaAddForeignKeyConstraintChange();
        change.setBaseTableName("person");
        change.setBaseColumnNames("parent");
        change.setConstraintName("fk_person_parent");
        change.setReferencedColumnNames("id");
        change.setReferencedTableName("person");

        PerconaToolkitVersion version = PTOnlineSchemaChangeStatement.getVersion();
        Assertions.assertEquals("0.0.0", version.toString());
        Assertions.assertTrue(PTOnlineSchemaChangeStatement.available);

        setTargetTableName("person");
        assertPerconaChange("ADD CONSTRAINT fk_person_parent FOREIGN KEY (parent) REFERENCES _person_new (id)", change.generateStatements(getDatabase()));
    }

    @Test
    public void testSelfReferencingForeignKeyPT3() {
        PerconaAddForeignKeyConstraintChange change = new PerconaAddForeignKeyConstraintChange();
        change.setBaseTableName("person");
        change.setBaseColumnNames("parent");
        change.setConstraintName("fk_person_parent");
        change.setReferencedColumnNames("id");
        change.setReferencedTableName("person");

        PTOnlineSchemaChangeStatement.perconaToolkitVersion = new PerconaToolkitVersion("3.0.12");
        PerconaToolkitVersion version = PTOnlineSchemaChangeStatement.getVersion();
        Assertions.assertEquals("3.0.12", version.toString());
        Assertions.assertTrue(PTOnlineSchemaChangeStatement.available);

        setTargetTableName("person");
        assertPerconaChange("ADD CONSTRAINT fk_person_parent FOREIGN KEY (parent) REFERENCES _person_new (id)", change.generateStatements(getDatabase()));
    }
}

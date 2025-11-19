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

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import liquibase.change.Change;
import liquibase.change.core.RawSQLChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.precondition.Precondition;
import liquibase.precondition.core.NotPrecondition;
import liquibase.precondition.core.PreconditionContainer;
import liquibase.precondition.core.SqlPrecondition;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;

@ExtendWith(RestoreSystemPropertiesExtension.class)
public class ChangeLogParserTest {

    private ResourceAccessor resourceAccessor;

    @BeforeEach
    public void setup() throws IOException {
        String packagePath = ChangeLogParserTest.class.getPackage().getName().replaceAll("\\.", "/");
        resourceAccessor = new DirectoryResourceAccessor(
                new File("target/test-classes/" + packagePath + "/changelog/")
                        .getCanonicalFile());
    }

    private DatabaseChangeLog loadChangeLog(String filename) throws Exception {
        ChangeLogParserFactory parserFactory = ChangeLogParserFactory.getInstance();
        ChangeLogParser parser = parserFactory.getParser(filename, resourceAccessor);
        return parser.parse(filename, new ChangeLogParameters(), resourceAccessor);
    }

    private static void assertChange(Change change, Class<? extends PerconaChange> type, Boolean usePercona,
            String perconaOptions) {
        Assertions.assertEquals(type, change.getClass());
        Assertions.assertEquals(usePercona, ((PerconaChange)change).getUsePercona());
        Assertions.assertEquals(perconaOptions, ((PerconaChange)change).getPerconaOptions());
    }

    private static void assertChangeLog(DatabaseChangeLog changelog) {
        Assertions.assertEquals(4, changelog.getChangeSets().size());
        Change change = changelog.getChangeSets().get(1).getChanges().get(0);
        assertChange(change, PerconaAddColumnChange.class, Boolean.FALSE, null);
        change = changelog.getChangeSets().get(2).getChanges().get(0);
        assertChange(change, PerconaAddColumnChange.class, null, "--foo");

        ChangeSet changeSet = changelog.getChangeSets().get(3);
        Assertions.assertEquals(PreconditionContainer.FailOption.WARN, changeSet.getPreconditions().getOnFail());
        Assertions.assertInstanceOf(NotPrecondition.class, changeSet.getPreconditions().getNestedPreconditions().get(0));
        Assertions.assertEquals("Comments should go after the precondition. Otherwise, Liquibase returns an error.", changeSet.getComments());
    }

    @Test
    public void testReadLiquibaseUsePerconaFlagYAML() throws Exception {
        DatabaseChangeLog changelog = loadChangeLog("test-changelog.yaml");
        assertChangeLog(changelog);
    }

    @Test
    public void testReadLiquibaseUsePerconaFlagXML() throws Exception {
        DatabaseChangeLog changelog = loadChangeLog("test-changelog.xml");
        assertChangeLog(changelog);
    }

    @Test
    public void testReadLiquibaseUsePerconaFlagSQL() throws Exception {
        DatabaseChangeLog changelog = loadChangeLog("test-changelog.sql");
        Assertions.assertEquals(5, changelog.getChangeSets().size());

        // changeset 1
        ChangeSet changeSet = changelog.getChangeSets().get(0);
        Change change = changeSet.getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, null, null);
        Assertions.assertEquals(1, changeSet.getRollback().getChanges().size());
        Change rollback = changeSet.getRollback().getChanges().get(0);
        assertChange(rollback, PerconaRawSQLChange.class, null, null);

        // changeset 2
        changeSet = changelog.getChangeSets().get(1);
        change = changeSet.getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, Boolean.FALSE, null);
        rollback = changeSet.getRollback().getChanges().get(0);
        assertChange(rollback, PerconaRawSQLChange.class, Boolean.FALSE, null);

        // changeset 3
        changeSet = changelog.getChangeSets().get(2);
        change = changeSet.getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, null, "--foo");
        rollback = changeSet.getRollback().getChanges().get(0);
        assertChange(rollback, PerconaRawSQLChange.class, null, "--foo");

        // changeset 4
        changeSet = changelog.getChangeSets().get(3);
        change = changeSet.getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, Boolean.TRUE, null);
        rollback = changeSet.getRollback().getChanges().get(0);
        assertChange(rollback, PerconaRawSQLChange.class, Boolean.TRUE, null);

        // changeset 5 - with preconditions
        changeSet = changelog.getChangeSets().get(4);
        change = changeSet.getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, null, null);
        Assertions.assertEquals(PreconditionContainer.FailOption.WARN, changeSet.getPreconditions().getOnFail());
        Precondition precondition = changeSet.getPreconditions().getNestedPreconditions().get(0);
        Assertions.assertInstanceOf(SqlPrecondition.class, precondition);
        Assertions.assertEquals("SELECT COUNT(*) FROM example_table", ((SqlPrecondition) precondition).getSql());
        Assertions.assertEquals("/* Comments should go after the precondition. Otherwise, Liquibase returns an error. */", changeSet.getComments());
    }

    @Test
    public void testReadLiquibaseUsePerconaFlagSQL_defaultOff() throws Exception {
        System.setProperty(Configuration.DEFAULT_ON, "false");
        DatabaseChangeLog changelog = loadChangeLog("test-changelog.sql");
        Assertions.assertEquals(5, changelog.getChangeSets().size());

        // changeset 1
        ChangeSet changeSet = changelog.getChangeSets().get(0);
        Change change = changeSet.getChanges().get(0);
        Assertions.assertSame(RawSQLChange.class, change.getClass());
        Assertions.assertFalse(change instanceof PerconaRawSQLChange);
        Assertions.assertEquals(1, changeSet.getRollback().getChanges().size());
        Change rollback = changeSet.getRollback().getChanges().get(0);
        Assertions.assertSame(RawSQLChange.class, rollback.getClass());
        Assertions.assertFalse(rollback instanceof PerconaRawSQLChange);

        // changeset 2
        changeSet = changelog.getChangeSets().get(1);
        change = changeSet.getChanges().get(0);
        Assertions.assertSame(RawSQLChange.class, change.getClass());
        Assertions.assertFalse(change instanceof PerconaRawSQLChange);
        rollback = changeSet.getRollback().getChanges().get(0);
        Assertions.assertSame(RawSQLChange.class, rollback.getClass());
        Assertions.assertFalse(rollback instanceof PerconaRawSQLChange);

        // changeset 3
        changeSet = changelog.getChangeSets().get(2);
        change = changeSet.getChanges().get(0);
        Assertions.assertSame(RawSQLChange.class, change.getClass());
        Assertions.assertFalse(change instanceof PerconaRawSQLChange);
        rollback = changeSet.getRollback().getChanges().get(0);
        Assertions.assertSame(RawSQLChange.class, rollback.getClass());
        Assertions.assertFalse(rollback instanceof PerconaRawSQLChange);

        // changeset 4 - the only one, that explicitly uses percona
        changeSet = changelog.getChangeSets().get(3);
        change = changeSet.getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, Boolean.TRUE, null);
        rollback = changeSet.getRollback().getChanges().get(0);
        assertChange(rollback, PerconaRawSQLChange.class, Boolean.TRUE, null);

        // changeset 5 - with preconditions
        changeSet = changelog.getChangeSets().get(4);
        change = changeSet.getChanges().get(0);
        Assertions.assertSame(RawSQLChange.class, change.getClass());
        Assertions.assertFalse(change instanceof PerconaRawSQLChange);
        Assertions.assertEquals(PreconditionContainer.FailOption.WARN, changeSet.getPreconditions().getOnFail());
        Precondition precondition = changeSet.getPreconditions().getNestedPreconditions().get(0);
        Assertions.assertInstanceOf(SqlPrecondition.class, precondition);
        Assertions.assertEquals("SELECT COUNT(*) FROM example_table", ((SqlPrecondition) precondition).getSql());
    }
}

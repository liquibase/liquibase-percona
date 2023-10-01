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

import liquibase.change.Change;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;

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
        Assertions.assertEquals(3, changelog.getChangeSets().size());
        Change change = changelog.getChangeSets().get(1).getChanges().get(0);
        assertChange(change, PerconaAddColumnChange.class, Boolean.FALSE, null);
        change = changelog.getChangeSets().get(2).getChanges().get(0);
        assertChange(change, PerconaAddColumnChange.class, null, "--foo");
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
        Assertions.assertEquals(3, changelog.getChangeSets().size());

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
    }
}

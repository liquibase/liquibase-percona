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
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.Scope;
import liquibase.Scope.ScopedRunnerWithReturn;
import liquibase.change.Change;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ResourceAccessor;
import liquibase.sdk.resource.MockResourceAccessor;
import liquibase.util.FileUtil;

public class ChangeLogParserTest {

    private ResourceAccessor resourceAccessor;

    @BeforeEach
    public void setup() throws IOException {
        Map<String, String> data = new HashMap<>();

        data.put("test-changelog.xml",
                FileUtil.getContents(new File("src/test/resources/liquibase/ext/percona/changelog/test-changelog.xml")));
        data.put("test-changelog.yaml",
                FileUtil.getContents(new File("src/test/resources/liquibase/ext/percona/changelog/test-changelog.yaml")));
        data.put("test-changelog.sql",
                FileUtil.getContents(new File("src/test/resources/liquibase/ext/percona/changelog/test-changelog.sql")));
        data.put("raw.githubusercontent.com/liquibase/liquibase-percona/liquibase-percona-2.0.0/src/main/resources/dbchangelog-ext-liquibase-percona.xsd",
                FileUtil.getContents(new File("src/main/resources/dbchangelog-ext-liquibase-percona.xsd")));
        data.put("www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd",
                readLiquibaseSchema("www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd"));

        resourceAccessor = new MockResourceAccessor(data);
    }

    private static String readLiquibaseSchema(String path) throws IOException {
        char[] buffer = new char[8192];
        StringBuilder sb = new StringBuilder(66000);
        try (Reader in = new InputStreamReader(ChangeLogParserTest.class.getResourceAsStream("/" + path), StandardCharsets.UTF_8)) {
            int count = in.read(buffer);
            while (count > 0) {
                sb.append(buffer, 0, count);
                count = in.read(buffer);
            }
        }
        return sb.toString();
    }

    private DatabaseChangeLog loadChangeLog(String filename) throws Exception {
        Map<String, Object> objects = new HashMap<>();
        objects.put(Scope.Attr.resourceAccessor.name(), resourceAccessor);
        return Scope.child(objects,
                new ScopedRunnerWithReturn<DatabaseChangeLog>() {
                public DatabaseChangeLog run() throws LiquibaseException {
                    ChangeLogParserFactory parserFactory = ChangeLogParserFactory.getInstance();
                    ChangeLogParser parser = parserFactory.getParser(filename, resourceAccessor);
                    return parser.parse(filename, new ChangeLogParameters(), resourceAccessor);
            }
        });
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
        Change change = changelog.getChangeSets().get(0).getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, null, null);
        change = changelog.getChangeSets().get(1).getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, Boolean.FALSE, null);
        change = changelog.getChangeSets().get(2).getChanges().get(0);
        assertChange(change, PerconaRawSQLChange.class, null, "--foo");
    }
}

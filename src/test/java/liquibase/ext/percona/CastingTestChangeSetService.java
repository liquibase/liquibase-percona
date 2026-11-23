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

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changeset.StandardChangeSetService;
import liquibase.database.ObjectQuotingStrategy;

/**
 * Stands in for a platform's own changeset service: it creates a {@link ChangeSet} subclass and expects to get that
 * subclass back. The plugin factory has no public unregister, so the service is switched off through its priority.
 */
public class CastingTestChangeSetService extends StandardChangeSetService {
    static final String END_DELIMITER = "//";

    private static volatile boolean active;

    static void setActive(boolean active) {
        CastingTestChangeSetService.active = active;
    }

    @Override
    public int getPriority() {
        return active ? PRIORITY_DEFAULT + 1000 : PRIORITY_NOT_APPLICABLE;
    }

    @Override
    public ChangeSet createChangeSet(String id, String author, boolean alwaysRun, boolean runOnChange,
            String filePath, String contextFilter, String dbmsList, String runWith, String runWithSpoolFile,
            boolean runInTransaction, ObjectQuotingStrategy quotingStrategy, DatabaseChangeLog databaseChangeLog) {
        return new CastingTestChangeSet(id, author, alwaysRun, runOnChange, filePath, contextFilter, dbmsList, runWith,
                runWithSpoolFile, runInTransaction, quotingStrategy, databaseChangeLog);
    }

    @Override
    public String getEndDelimiter(ChangeSet changeSet) {
        return ((CastingTestChangeSet) changeSet).endDelimiter;
    }

    static class CastingTestChangeSet extends ChangeSet {
        private final String endDelimiter = END_DELIMITER;

        CastingTestChangeSet(String id, String author, boolean alwaysRun, boolean runOnChange, String filePath,
                String contextFilter, String dbmsList, String runWith, String runWithSpoolFile,
                boolean runInTransaction, ObjectQuotingStrategy quotingStrategy, DatabaseChangeLog databaseChangeLog) {
            super(id, author, alwaysRun, runOnChange, filePath, contextFilter, dbmsList, runWith, runWithSpoolFile,
                    runInTransaction, quotingStrategy, databaseChangeLog);
        }
    }
}

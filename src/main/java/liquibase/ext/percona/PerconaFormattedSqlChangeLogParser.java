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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import liquibase.Scope;
import liquibase.change.Change;
import liquibase.change.core.RawSQLChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.logging.Logger;
import liquibase.parser.core.formattedsql.FormattedSqlChangeLogParser;
import liquibase.resource.ResourceAccessor;

public class PerconaFormattedSqlChangeLogParser extends FormattedSqlChangeLogParser {
    private static final Logger LOG = Scope.getCurrentScope().getLog(PerconaFormattedSqlChangeLogParser.class);

    private static final Pattern USE_PERCONA_PATTERN = Pattern.compile("(?im)^\\s*\\-\\-\\s*liquibasePercona:usePercona=\"(false|true)\"\\s*$");
    private static final Pattern PERCONA_OPTIONS_PATTERN = Pattern.compile("(?im)^\\s*\\-\\-\\s*liquibasePercona:perconaOptions=\"(.*)\"\\s*$");

    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT + 50;
    }

    @Override
    public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters, ResourceAccessor resourceAccessor) throws ChangeLogParseException {
        DatabaseChangeLog changeLog = super.parse(physicalChangeLogLocation, changeLogParameters, resourceAccessor);

        for (int i = 0; i < changeLog.getChangeSets().size(); i++) {
            ChangeSet changeSet = changeLog.getChangeSets().get(i);

            String contextFilter = null;
            if (!changeSet.getContextFilter().isEmpty()) {
                contextFilter = changeSet.getContextFilter().toString();
            }

            ChangeSet perconaChangeSet = new ChangeSet(changeSet.getId(), changeSet.getAuthor(), changeSet.isAlwaysRun(),
                    changeSet.isRunOnChange(), changeSet.getFilePath(), contextFilter,
                    changeSet.getDbmsSet() == null ? null : changeSet.getDbmsSet().stream().collect(Collectors.joining(",")),
                    changeSet.getRunWith(), changeSet.getRunWithSpoolFile(), changeSet.isRunInTransaction(), changeSet.getObjectQuotingStrategy(),
                    changeLog);

            LOG.fine(String.format("Changeset %s::%s::%s contains %d changes and %d rollback changes",
                    changeSet.getFilePath(), changeSet.getId(), changeSet.getAuthor(),
                    changeSet.getChanges().size(),
                    changeSet.getRollback().getChanges().size()));

            boolean usePerconaChangeSet = Configuration.getDefaultOn();
            for (Change change : changeSet.getChanges()) {
                RawSQLChange rawSQLChange = (RawSQLChange) change;
                PerconaRawSQLChange perconaChange = convert(rawSQLChange);
                perconaChangeSet.addChange(perconaChange);

                String sql = perconaChange.getSql();
                Matcher usePerconaMatcher = USE_PERCONA_PATTERN.matcher(sql);
                if (usePerconaMatcher.find()) {
                    boolean usePercona = Boolean.parseBoolean(usePerconaMatcher.group(1));
                    perconaChange.setUsePercona(usePercona);
                    usePerconaChangeSet = usePerconaChangeSet || usePercona;
                }

                Matcher perconaOptionsMatcher = PERCONA_OPTIONS_PATTERN.matcher(sql);
                if (perconaOptionsMatcher.find()) {
                    perconaChange.setPerconaOptions(perconaOptionsMatcher.group(1));
                }
            }

            if (!usePerconaChangeSet) {
                LOG.fine(String.format("Not using percona toolkit for changeset %s::%s::%s, because no change requests it",
                        perconaChangeSet.getFilePath(), perconaChangeSet.getId(), perconaChangeSet.getAuthor()));
                continue;
            }

            for (Change change : changeSet.getRollback().getChanges()) {
                RawSQLChange rawSQLChange = (RawSQLChange) change;
                PerconaRawSQLChange rollbackChange = convert(rawSQLChange);

                if (!perconaChangeSet.getChanges().isEmpty()) {
                    if (perconaChangeSet.getChanges().size() > 1) {
                        LOG.warning(String.format("The changeset %s::%s::%s contains %d changes - using the first " +
                                        "one to copy percona options",
                                perconaChangeSet.getFilePath(), perconaChangeSet.getId(), perconaChangeSet.getAuthor(),
                                perconaChangeSet.getChanges().size()));
                    }

                    PerconaRawSQLChange forwardChange = (PerconaRawSQLChange) perconaChangeSet.getChanges().get(0);
                    rollbackChange.setUsePercona(forwardChange.getUsePercona());
                    rollbackChange.setPerconaOptions(forwardChange.getPerconaOptions());
                } else {
                    LOG.warning(String.format("The changeset %s::%s::%s contains 0 changes, but contains rollback - " +
                                    "using default percona options",
                            perconaChangeSet.getFilePath(), perconaChangeSet.getId(), perconaChangeSet.getAuthor()));
                }

                perconaChangeSet.getRollback().getChanges().add(rollbackChange);
            }

            changeLog.getChangeSets().set(i, perconaChangeSet);
        }

        return changeLog;
    }

    private static PerconaRawSQLChange convert(RawSQLChange rawSQLChange) {
        PerconaRawSQLChange perconaChange = new PerconaRawSQLChange();
        perconaChange.setSql(rawSQLChange.getSql());
        perconaChange.setSplitStatements(rawSQLChange.isSplitStatements());
        perconaChange.setStripComments(rawSQLChange.isStripComments());
        perconaChange.setEndDelimiter(rawSQLChange.getEndDelimiter());
        perconaChange.setRerunnable(rawSQLChange.isRerunnable());
        perconaChange.setComment(rawSQLChange.getComment());
        perconaChange.setDbms(rawSQLChange.getDbms());
        return perconaChange;
    }
}

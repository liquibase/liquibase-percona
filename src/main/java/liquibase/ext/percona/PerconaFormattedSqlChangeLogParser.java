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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liquibase.Scope;
import liquibase.change.Change;
import liquibase.change.core.RawSQLChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.logging.Logger;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
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

    /**
     * Parses the file with the next formatted SQL parser in line, so the directives a platform's own parser adds keep
     * working, then swaps the raw SQL changes of each changeset for percona ones in place.
     */
    @Override
    public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters, ResourceAccessor resourceAccessor) throws ChangeLogParseException {
        ChangeLogParser delegate = findDelegate(physicalChangeLogLocation, resourceAccessor);
        DatabaseChangeLog changeLog = delegate == null
                ? super.parse(physicalChangeLogLocation, changeLogParameters, resourceAccessor)
                : delegate.parse(physicalChangeLogLocation, changeLogParameters, resourceAccessor);

        for (ChangeSet changeSet : changeLog.getChangeSets()) {
            LOG.fine(String.format("Changeset %s::%s::%s contains %d changes and %d rollback changes",
                    changeSet.getFilePath(), changeSet.getId(), changeSet.getAuthor(),
                    changeSet.getChanges().size(),
                    changeSet.getRollback().getChanges().size()));

            if (!onlyRawSqlChanges(changeSet)) {
                // other change types, or a changeset already converted when its own file was parsed
                continue;
            }

            List<Change> originalChanges = new ArrayList<>(changeSet.getChanges());
            List<PerconaRawSQLChange> perconaChanges = new ArrayList<>();
            boolean usePerconaChangeSet = Configuration.getDefaultOn();
            for (Change change : originalChanges) {
                PerconaRawSQLChange perconaChange = convert((RawSQLChange) change);
                perconaChanges.add(perconaChange);

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
                        changeSet.getFilePath(), changeSet.getId(), changeSet.getAuthor()));
                continue;
            }

            List<Change> rollbackChanges = new ArrayList<>();
            for (Change change : changeSet.getRollback().getChanges()) {
                if (!(change instanceof RawSQLChange)) {
                    rollbackChanges.add(change);
                    continue;
                }
                PerconaRawSQLChange rollbackChange = convert((RawSQLChange) change);

                if (!perconaChanges.isEmpty()) {
                    if (perconaChanges.size() > 1) {
                        LOG.warning(String.format("The changeset %s::%s::%s contains %d changes - using the first " +
                                        "one to copy percona options",
                                changeSet.getFilePath(), changeSet.getId(), changeSet.getAuthor(),
                                perconaChanges.size()));
                    }

                    PerconaRawSQLChange forwardChange = perconaChanges.get(0);
                    rollbackChange.setUsePercona(forwardChange.getUsePercona());
                    rollbackChange.setPerconaOptions(forwardChange.getPerconaOptions());
                } else {
                    LOG.warning(String.format("The changeset %s::%s::%s contains 0 changes, but contains rollback - " +
                                    "using default percona options",
                            changeSet.getFilePath(), changeSet.getId(), changeSet.getAuthor()));
                }

                rollbackChanges.add(rollbackChange);
            }

            changeSet.removeAllChanges(originalChanges);
            perconaChanges.forEach(changeSet::addChange);
            changeSet.getRollback().setChanges(rollbackChanges);
        }

        return changeLog;
    }

    /**
     * The parser that would read this file if this one were not installed, or {@code null} when that is the core
     * formatted SQL parser, which {@code super.parse} already is.
     */
    private ChangeLogParser findDelegate(String physicalChangeLogLocation, ResourceAccessor resourceAccessor) {
        return ChangeLogParserFactory.getInstance().getParsers().stream()
                .filter(parser -> !(parser instanceof PerconaFormattedSqlChangeLogParser))
                .filter(parser -> parser.supports(physicalChangeLogLocation, resourceAccessor))
                .findFirst()
                .filter(parser -> parser.getClass() != FormattedSqlChangeLogParser.class)
                .orElse(null);
    }

    private static boolean onlyRawSqlChanges(ChangeSet changeSet) {
        return changeSet.getChanges().stream()
                .allMatch(change -> change instanceof RawSQLChange && !(change instanceof PerconaRawSQLChange));
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

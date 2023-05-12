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

import liquibase.change.Change;
import liquibase.change.core.RawSQLChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.parser.core.formattedsql.FormattedSqlChangeLogParser;
import liquibase.resource.ResourceAccessor;
import liquibase.servicelocator.PrioritizedService;

public class PerconaFormattedSqlChangeLogParser extends FormattedSqlChangeLogParser {
    @Override
    public int getPriority() {
        return PrioritizedService.PRIORITY_DEFAULT + 50;
    }

    @Override
    public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters, ResourceAccessor resourceAccessor) throws ChangeLogParseException {
        Pattern usePerconaPattern = Pattern.compile("(?im)^\\s*\\-\\-\\s*liquibasePercona:usePercona=\"(false|true)\"\\s*$");
        Pattern perconaOptionsPattern = Pattern.compile("(?im)^\\s*\\-\\-\\s*liquibasePercona:perconaOptions=\"(.*)\"\\s*$");

        DatabaseChangeLog changeLog = super.parse(physicalChangeLogLocation, changeLogParameters, resourceAccessor);

        for (int i = 0; i < changeLog.getChangeSets().size(); i++) {
            ChangeSet changeSet = changeLog.getChangeSets().get(i);

            ChangeSet perconaChangeSet = new ChangeSet(changeSet.getId(), changeSet.getAuthor(), changeSet.isAlwaysRun(),
                    changeSet.isRunOnChange(), changeSet.getFilePath(), changeSet.getContextFilter().toString(),
                    changeSet.getDbmsSet() == null ? null : changeSet.getDbmsSet().stream().collect(Collectors.joining(",")),
                    changeSet.getRunWith(), changeSet.getRunWithSpoolFile(), changeSet.isRunInTransaction(), changeSet.getObjectQuotingStrategy(),
                    changeLog);

            for (Change change : changeSet.getChanges()) {
                RawSQLChange rawSQLChange = (RawSQLChange) change;
                PerconaRawSQLChange perconaChange = new PerconaRawSQLChange();
                perconaChange.setSql(rawSQLChange.getSql());
                perconaChange.setSplitStatements(rawSQLChange.isSplitStatements());
                perconaChange.setStripComments(rawSQLChange.isStripComments());
                perconaChange.setEndDelimiter(rawSQLChange.getEndDelimiter());
                perconaChange.setRerunnable(rawSQLChange.isRerunnable());
                perconaChange.setComment(rawSQLChange.getComment());
                perconaChange.setDbms(rawSQLChange.getDbms());
                perconaChangeSet.addChange(perconaChange);

                String sql = perconaChange.getSql();
                Matcher usePerconaMatcher = usePerconaPattern.matcher(sql);
                if (usePerconaMatcher.find()) {
                    perconaChange.setUsePercona(Boolean.valueOf(usePerconaMatcher.group(1)));
                }

                Matcher perconaOptionsMatcher = perconaOptionsPattern.matcher(sql);
                if (perconaOptionsMatcher.find()) {
                    perconaChange.setPerconaOptions(perconaOptionsMatcher.group(1));
                }
            }
            changeLog.getChangeSets().set(i, perconaChangeSet);
        }

        return changeLog;
    }
}

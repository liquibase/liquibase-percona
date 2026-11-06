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

import liquibase.change.core.TagDatabaseChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.parser.core.formattedsql.FormattedSqlChangeLogParser;
import liquibase.resource.ResourceAccessor;

/**
 * Stands in for a platform's own formatted SQL parser: ranked between the core parser and the percona parser, it
 * resolves a {@code --testInclude} directive the core parser ignores and adds a non-SQL change to one changeset.
 */
public class DelegatingTestFormattedSqlChangeLogParser extends FormattedSqlChangeLogParser {
    private static final Pattern TEST_INCLUDE = Pattern.compile("(?i)^\\s*--\\s*testInclude\\s+file:(\\S+)\\s*$");

    /** Every changeset this parser created, so tests can check the percona parser kept the same instances. */
    static final List<ChangeSet> CREATED = new ArrayList<>();

    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT + 10;
    }

    @Override
    public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters,
            ResourceAccessor resourceAccessor) throws ChangeLogParseException {
        DatabaseChangeLog changeLog = super.parse(physicalChangeLogLocation, changeLogParameters, resourceAccessor);
        for (ChangeSet changeSet : changeLog.getChangeSets()) {
            if (changeSet.getFilePath().endsWith(physicalChangeLogLocation)) {
                CREATED.add(changeSet);
            }
            if ("tagged".equals(changeSet.getId())) {
                TagDatabaseChange tag = new TagDatabaseChange();
                tag.setTag("v1");
                changeSet.addChange(tag);
            }
        }
        return changeLog;
    }

    @Override
    protected boolean handleAdditionalLines(DatabaseChangeLog changeLog, ResourceAccessor resourceAccessor, String line)
            throws ChangeLogParseException {
        Matcher matcher = TEST_INCLUDE.matcher(line);
        if (!matcher.matches()) {
            return false;
        }
        String included = matcher.group(1);
        try {
            DatabaseChangeLog child = ChangeLogParserFactory.getInstance().getParser(included, resourceAccessor)
                    .parse(included, new ChangeLogParameters(), resourceAccessor);
            child.getChangeSets().forEach(changeLog::addChangeSet);
        } catch (LiquibaseException e) {
            throw new ChangeLogParseException(e);
        }
        return true;
    }
}

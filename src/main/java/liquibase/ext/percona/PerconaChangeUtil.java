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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;

public class PerconaChangeUtil {

    private static Logger log = LogFactory.getInstance().getLog();
    private static Map<String, Boolean> alreadyLogged = new HashMap<String, Boolean>();

    /**
     * Determines whether *SQL (updateSQL/rollbackSQL) is executed or whether
     * the statements should be executed directly.
     * @param database the database
     * @return <code>true</code> if dry-run is enabled and the statements should *not* be executed.
     */
    private static boolean isDryRun(Database database) {
        Executor executor = ExecutorService.getInstance().getExecutor(database);
        if (executor instanceof LoggingExecutor) {
            return true;
        }
        return false;
    }

    public static SqlStatement[] generateStatements(final String changeName,
            Database database, SqlStatement[] originalStatements,
            final String tableName, final String alterStatement) {
        PerconaChange change = new PerconaChange() {
            @Override
            public Boolean getUsePercona() {
                return true;
            }
            @Override
            public String getChangeSkipName() {
                return changeName;
            }
            @Override
            public String getTargetTableName() {
                return tableName;
            }
            @Override
            public String generateAlterStatement(Database database) {
                return alterStatement;
            }
        };
        return generateStatements(change, database, originalStatements);
    }

    public static SqlStatement[] generateStatements(PerconaChange change,
            Database database, SqlStatement[] originalStatements) {

        if (Boolean.FALSE.equals(change.getUsePercona())) {
            String changeSetId = "unknown changeset id";
            if (change instanceof Change) {
                ChangeSet changeSet = ((Change)change).getChangeSet();
                if (changeSet != null) {
                    changeSetId = changeSet.getId() + ":" + changeSet.getAuthor();
                }
            }
            log.debug("Not using percona toolkit, because usePercona flag is false for " + changeSetId + ":" + change.toString());
            return originalStatements;
        }
        if (Configuration.skipChange(change.getChangeSkipName())) {
            maybeLog("Not using percona toolkit, because skipChange for "
                    + change.getChangeSkipName() + " is active (property: " + Configuration.SKIP_CHANGES + ")!");
            return originalStatements;
        }

        List<SqlStatement> statements = new ArrayList<SqlStatement>(Arrays.asList(originalStatements));

        if (database instanceof MySQLDatabase) {
            if (PTOnlineSchemaChangeStatement.isAvailable()) {
                PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement(
                        change.getTargetTableName(),
                        change.generateAlterStatement(database));

                if (isDryRun(database)) {
                    CommentStatement commentStatement = new CommentStatement(statement.printCommand(database));

                    if (Configuration.noAlterSqlDryMode()) {
                        statements.clear();
                        statements.add(0, commentStatement);
                    } else {
                        statements.add(0, commentStatement);
                        statements.add(1, new CommentStatement("Instead of the following statements, pt-online-schema-change will be used"));
                    }
                } else {
                    statements.clear();
                    statements.add(statement);
                }
            } else {
                if (Configuration.failIfNoPT()) {
                    throw new RuntimeException("No percona toolkit found!");
                }
                maybeLog("Not using percona toolkit, because it is not available!");
            }
        }

        return statements.toArray(new SqlStatement[statements.size()]);
    }

    /**
     * Logs a warning only if it hasn't been logged yet to prevent logging the same warning over and over again.
     */
    private static void maybeLog(String message) {
        if (!alreadyLogged.containsKey(message)) {
            log.warning(message);
            alreadyLogged.put(message, Boolean.TRUE);
        }
    }

    /**
     * In case the foreign key is self-referencing the table itself, we have to deal with this
     * the temporary percona table name. Since percona copies the old table and performas the alters
     * on the copy, we need to reference the copy in that case ("_new" suffix).
     *
     * Since this bug is scheduled to be fixed with pt 2.2.21, the workaround will be applied
     * only for earlier versions.
     *
     * @param database
     * @return
     *
     * @see <a href="https://bugs.launchpad.net/percona-toolkit/+bug/1393961">pt-online-schema-change fails with self-referential foreign key</a>
     */
    public static String resolveReferencedPerconaTableName(String baseTableName, String referencedTableName) {
        if (baseTableName != null && baseTableName.equals(referencedTableName)
                && !PTOnlineSchemaChangeStatement.getVersion().isGreaterOrEqualThan("2.2.21")) {
            log.warning("Applying workaround for pt-osc bug https://bugs.launchpad.net/percona-toolkit/+bug/1393961 for table " + baseTableName);
            return "_" + referencedTableName + "_new";
        }

        return referencedTableName;
    }

}

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

    public static SqlStatement[] generateStatements(String changeName,
            Database database, SqlStatement[] originalStatements,
            String tableName, String alterStatement) {

        if (Configuration.skipChange(changeName)) {
            maybeLog("Not using percona toolkit, because skipChange for "
                    + changeName + " is active (property: " + Configuration.SKIP_CHANGES + ")!");
            return originalStatements;
        }

        List<SqlStatement> statements = new ArrayList<SqlStatement>(Arrays.asList(originalStatements));

        if (database instanceof MySQLDatabase) {
            if (PTOnlineSchemaChangeStatement.isAvailable()) {
                PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement(
                        tableName,
                        alterStatement);

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
}

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

import java.util.List;
import java.util.Map;

import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.core.FindForeignKeyConstraintsStatement;

public class PerconaForeignKeyService {
    private static PerconaForeignKeyService instance = new PerconaForeignKeyService();
    private Logger log = LogFactory.getInstance().getLog();
    private boolean enabled = true;

    public static PerconaForeignKeyService getInstance() {
        return instance;
    }

    /**
     * The functionality can be disabled - this is especially useful for unit tests,
     * where there is no real database to query.
     */
    public void disable() {
        if (enabled) {
            log.warning("Disabled dynamic foreign key name resolution - DropForeignKeyConstraint might not work with pt-osc");
            enabled = false;
        }
    }

    /**
     * pt-osc changes the constraint names to avoid collisions.
     * It prefixes the original name with 0, 1 or 2 underscores.
     *
     * @param database
     * @return
     */
    public String determineCurrentConstraintName(Database database, PerconaDropForeignKeyConstraintChange change) {
        // start with best guess without looking at the database
        String constraintName = change.getConstraintName();

        if (enabled && !PerconaChangeUtil.isDryRun(database) && PerconaChangeUtil.isConnected(database)) {
            constraintName = findForeignKey(database, change);
        }
        return prefixConstraintName(constraintName);
    }

    private String findForeignKey(Database database, PerconaDropForeignKeyConstraintChange change) {
        log.debug("Searching for all foreign keys in table " + change.getBaseTableName());

        Executor executor = ExecutorService.getInstance().getExecutor(database);
        FindForeignKeyConstraintsStatement sql = new FindForeignKeyConstraintsStatement(change.getBaseTableCatalogName(),
                change.getBaseTableSchemaName(), change.getBaseTableName());
        try {
            List<Map<String, ?>> results = executor.queryForList(sql);
            for (Map<String, ?> result : results) {
                String baseTableName =
                        (String) result.get(FindForeignKeyConstraintsStatement.RESULT_COLUMN_BASE_TABLE_NAME);
                String constraintName =
                        (String) result.get(FindForeignKeyConstraintsStatement.RESULT_COLUMN_CONSTRAINT_NAME);
                log.debug("Found FK: " + baseTableName + "." + constraintName);

                if (baseTableName.equalsIgnoreCase(change.getBaseTableName())
                    && constraintName.endsWith(change.getConstraintName())) {
                        log.debug("Found current foreign key constraint " + constraintName);
                        return constraintName;
                }
            }
        } catch (DatabaseException e) {
            throw new UnexpectedLiquibaseException("Failed to find foreign keys for table: " + change.getBaseTableName(), e);
        }

        log.warning("No foreign key with name " + change.getConstraintName() + " found.");
        return change.getConstraintName();
    }

    /**
     * We use the same algorithm as pt-osc.
     *
     * <blockquote>
     * So we do replacements when constraint names:<br>
     * Has 2 _, we remove them<br>
     * Has 1 _, we add one to make 2<br>
     * Has no _, we add one to make 1<br>
     * </blockquote>
     * See <a href="https://github.com/percona/percona-toolkit/blob/5498a4da8170e782ed0b293555a08730e1d83f38/bin/pt-online-schema-change#L10448-L10457">
     * pt-osc lines 10448ff.</a>
     *
     * @param constraintName
     * @return
     */
    private String prefixConstraintName(String constraintName) {
        if (constraintName != null && constraintName.startsWith("__")) {
            return constraintName.substring(2);
        } else {
            return "_" + constraintName;
        }
    }
}

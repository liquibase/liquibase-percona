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
import liquibase.database.Database;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;

public class ChangeUtil {
    /**
     * Determines whether *SQL (updateSQL/rollbackSQL) is executed or whether
     * the statements should be executed directly.
     * @param database the database
     * @return <code>true</code> if dry-run is enabled and the statements should *not* be executed.
     */
    public static boolean isDryRun(Database database) {
        Executor executor = ExecutorService.getInstance().getExecutor(database);
        if (executor instanceof LoggingExecutor) {
            return true;
        }
        return false;
    }

}

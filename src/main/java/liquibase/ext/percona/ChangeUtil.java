package liquibase.ext.percona;

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

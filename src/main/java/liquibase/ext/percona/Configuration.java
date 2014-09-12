package liquibase.ext.percona;

/**
 * Encapsulates runtime configuration for percona.
 */
public final class Configuration {
    /** Fail liquibase update if no percona toolkit is found. */
    public static final String FAIL_IF_NO_PT = "liquibase.percona.failIfNoPT";
    /** Do not generate the alter table statements in dry mode (updateSQL / rollbackSQL) */
    public static final String NO_ALTER_SQL_DRY_MODE = "liquibase.percona.noAlterSqlDryMode";

    public static boolean failIfNoPT() {
        return Boolean.getBoolean(FAIL_IF_NO_PT);
    }

    public static boolean noAlterSqlDryMode() {
        return Boolean.getBoolean(NO_ALTER_SQL_DRY_MODE);
    }
}

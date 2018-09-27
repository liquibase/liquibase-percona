package liquibase.ext.percona;

import liquibase.database.Database;

public interface PerconaChange {

    Boolean getUsePercona();

    String getChangeSkipName();

    String getTargetDatabaseName();

    String getTargetTableName();

    String generateAlterStatement(Database database);
}

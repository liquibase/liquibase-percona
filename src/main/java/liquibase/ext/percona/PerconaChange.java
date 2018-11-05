package liquibase.ext.percona;

import liquibase.database.Database;

public interface PerconaChange {

    Boolean getUsePercona();

    String getChangeName();

    String getTargetDatabaseName();

    String getTargetTableName();

    String generateAlterStatement(Database database);
}

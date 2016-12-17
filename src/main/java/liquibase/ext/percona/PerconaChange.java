package liquibase.ext.percona;

import liquibase.database.Database;

public interface PerconaChange {

    Boolean getUsePercona();

    String getChangeSkipName();

    String getTargetTableName();

    String generateAlterStatement(Database database);
}

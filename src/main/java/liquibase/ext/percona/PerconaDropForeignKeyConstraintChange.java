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

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.DropForeignKeyConstraintChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

/**
 * Subclasses the original {@link liquibase.change.core.DropForeignKeyConstraintChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = PerconaDropForeignKeyConstraintChange.NAME, description = "Drops an existing foreign key",
    priority = PerconaDropForeignKeyConstraintChange.PRIORITY, appliesTo = "foreignKey")
public class PerconaDropForeignKeyConstraintChange extends DropForeignKeyConstraintChange implements PerconaChange {
    public static final String NAME = "dropForeignKeyConstraint";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private Boolean usePercona;

    private String perconaOptions;

    /**
     * Generates the statements required for the drop foreign key constraint change.
     * In case of a MySQL database, percona toolkit will be used.
     * In case of generating the SQL statements for review (updateSQL) the command
     * will be added as a comment.
     * @param database the database
     * @return the list of statements
     * @see PTOnlineSchemaChangeStatement
     */
    @Override
    public SqlStatement[] generateStatements(Database database) {
        return PerconaChangeUtil.generateStatements(this,
                database,
                super.generateStatements(database));
    }

    @Override
    public String generateAlterStatement(Database database) {
        StringBuilder alter = new StringBuilder();

        String contraintName = PerconaConstraintsService.getInstance().determineCurrentConstraintName(database, this);

        alter.append("DROP FOREIGN KEY ");
        alter.append(database.escapeConstraintName(contraintName));

        return alter.toString();
    }

    @Override
    @DatabaseChangeProperty(requiredForDatabase = {})
    public Boolean getUsePercona() {
        return usePercona;
    }

    public void setUsePercona(Boolean usePercona) {
        this.usePercona = usePercona;
    }

    @Override
    @DatabaseChangeProperty(requiredForDatabase = {})
    public String getPerconaOptions() {
        return perconaOptions;
    }

    public void setPerconaOptions(String perconaOptions) {
        this.perconaOptions = perconaOptions;
    }

    @Override
    public String getChangeName() {
        return NAME;
    }

    @Override
    public String getTargetTableName() {
        return getBaseTableName();
    }

    @Override
    public String getTargetDatabaseName() {
        return getBaseTableCatalogName();
    }
}

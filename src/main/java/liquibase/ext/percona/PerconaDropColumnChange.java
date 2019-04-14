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
import liquibase.change.ColumnConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.DropColumnChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

/**
 * Subclasses the original {@link liquibase.change.core.DropColumnChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = PerconaDropColumnChange.NAME,
    description = "Drop an existing column",
    priority = PerconaDropColumnChange.PRIORITY,
    appliesTo = "column")
public class PerconaDropColumnChange extends DropColumnChange implements PerconaChange {
    public static final String NAME = "dropColumn";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private Boolean usePercona;

    /**
     * Generates the statements required for the drop column change.
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
        if (getColumns() != null && !getColumns().isEmpty()) {
            boolean first = true;
            for (ColumnConfig column : getColumns()) {
                if (!first) {
                    alter.append(", ");
                }
                alter.append("DROP COLUMN ").append(column.getName());
                first = false;
            }
        } else {
            alter.append("DROP COLUMN ").append(getColumnName());
        }
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
    public String getChangeName() {
        return NAME;
    }

    @Override
    public String getTargetTableName() {
        return getTableName();
    }

    @Override
    public String getTargetDatabaseName() {
        return getCatalogName();
    }
}

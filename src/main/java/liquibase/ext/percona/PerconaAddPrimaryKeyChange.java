package liquibase.ext.percona;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import liquibase.change.Change;

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
import liquibase.change.core.AddPrimaryKeyChange;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.statement.SqlStatement;
import liquibase.util.StringUtil;

@DatabaseChange(name = PerconaAddPrimaryKeyChange.NAME, description = "Adds creates a primary key out of an existing column or set of columns.",
    priority = PerconaAddPrimaryKeyChange.PRIORITY, appliesTo = "column")
public class PerconaAddPrimaryKeyChange extends AddPrimaryKeyChange implements PerconaChange {
    public static final String NAME = "addPrimaryKey";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    /**
     * Generates the statements required for the add primary key change.
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

        // in case there is a primary key already, we need to drop it first
        // this should be done with one single pt-osc call
        if (PerconaConstraintsService.getInstance().hasPrimaryKey(database, this)) {
            alter.append("DROP PRIMARY KEY, ");
        }

        alter.append("ADD PRIMARY KEY (");
        List<String> columns = StringUtil.splitAndTrim(getColumnNames(), ",");
        if (columns == null) columns = Collections.emptyList();
        alter.append(database.escapeColumnNameList(StringUtil.join(columns, ", ")));
        alter.append(')');

        return alter.toString();
    }

    @Override
    public String getTargetTableName() {
        return getTableName();
    }

    @Override
    public String getTargetDatabaseName() {
        return getCatalogName();
    }

    @Override
    protected Change[] createInverses() {
        return null;
    }

    //CPD-OFF - common PerconaChange implementation
    private Boolean usePercona;

    private String perconaOptions;

    @Override
    public String getChangeName() {
        return NAME;
    }

    @Override
    @DatabaseChangeProperty(requiredForDatabase = {})
    public Boolean getUsePercona() {
        return usePercona;
    }

    @Override
    public void setUsePercona(Boolean usePercona) {
        this.usePercona = usePercona;
    }

    @Override
    @DatabaseChangeProperty(requiredForDatabase = {})
    public String getPerconaOptions() {
        return perconaOptions;
    }

    @Override
    public void setPerconaOptions(String perconaOptions) {
        this.perconaOptions = perconaOptions;
    }

    @Override
    public Set<String> getSerializableFields() {
        Set<String> fields = new HashSet<>(super.getSerializableFields());
        fields.remove("usePercona");
        fields.remove("perconaOptions");
        return Collections.unmodifiableSet(fields);
    }

    @Override
    public ValidationErrors validate(Database database) {
        return PerconaChangeUtil.validate(super.validate(database), database);
    }

    @SuppressWarnings("PMD.UselessOverridingMethod")
    @Override
    public boolean supports(Database database) {
        // just using the same logic as super; in case we don't support this change for percona,
        // we'll anyway return just the original statements.
        // See liquibase.change.ChangeFactory#verifySupportsMethodImplementation for the verification logic.
        return super.supports(database);
    }
    //CPD-ON
}

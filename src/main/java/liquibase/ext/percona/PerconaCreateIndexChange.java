package liquibase.ext.percona;

import java.util.Collections;
import java.util.HashSet;

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

import java.util.Iterator;
import java.util.Set;

import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.CreateIndexChange;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.statement.SqlStatement;

@DatabaseChange(name = PerconaCreateIndexChange.NAME,
    description = "Creates an index on an existing column or set of columns.",
    priority = PerconaCreateIndexChange.PRIORITY, appliesTo = "index")
public class PerconaCreateIndexChange extends CreateIndexChange implements PerconaChange {
    public static final String NAME = "createIndex";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    @Override
    public SqlStatement[] generateStatements( Database database ) {
        return PerconaChangeUtil.generateStatements(this,
                    database,
                    super.generateStatements(database));
    }

    @Override
    public String generateAlterStatement( Database database ) {
        StringBuilder alter = new StringBuilder();

        alter.append( "ADD ");
        if (this.isUnique() != null && this.isUnique()) {
            alter.append( "UNIQUE " );
        }
        alter.append( "INDEX " );

        if (this.getIndexName() != null) {
            alter.append(database.escapeIndexName(this.getCatalogName(), this.getSchemaName(), this.getIndexName())).append(" ");
        }

        alter.append("(");
        Iterator<AddColumnConfig> iterator = this.getColumns().iterator();
        while (iterator.hasNext()) {
            AddColumnConfig column = iterator.next();
            if (Boolean.TRUE.equals(column.getComputed())) {
                // don't quote functions
                alter.append(column.getName());
            } else {
                String justColumnName = column.getName();
                String prefixLength = "";
                // maybe prefix length
                int prefixLengthIndex = justColumnName.indexOf('(');
                if (prefixLengthIndex > -1) {
                    prefixLength = justColumnName.substring(prefixLengthIndex);
                    justColumnName = justColumnName.substring(0, prefixLengthIndex);
                }

                alter.append(database.escapeColumnName(this.getCatalogName(), this.getSchemaName(), this.getTableName(), justColumnName));
                alter.append(prefixLength);
            }
            if (iterator.hasNext()) {
                alter.append(", ");
            }
        }
        alter.append(")");

        return alter.toString();
    }

    @Override
    protected Change[] createInverses() {
        PerconaDropIndexChange inverse = new PerconaDropIndexChange();
        inverse.setIndexName(getIndexName());
        inverse.setCatalogName(getCatalogName());
        inverse.setSchemaName(getSchemaName());
        inverse.setTableName(getTableName());

        return new Change[] { inverse };
    }

    @Override
    public String getTargetTableName() {
        return getTableName();
    }

    @Override
    public String getTargetDatabaseName() {
        return getCatalogName();
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

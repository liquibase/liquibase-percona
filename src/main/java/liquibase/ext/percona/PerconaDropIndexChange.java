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
import liquibase.change.core.DropIndexChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

@DatabaseChange(name = PerconaDropIndexChange.NAME,
    description = "Drops an existing index",
    priority = PerconaDropIndexChange.PRIORITY, appliesTo = "index")
public class PerconaDropIndexChange extends DropIndexChange implements PerconaChange {
    public static final String NAME = "dropIndex";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private Boolean usePercona;

    @Override
    public SqlStatement[] generateStatements( Database database )
    {
        return PerconaChangeUtil.generateStatements(this,
                    database,
                    super.generateStatements(database));
    }

    @Override
    public String generateAlterStatement( Database database )
    {
        StringBuilder alter = new StringBuilder();

        alter.append( "DROP ");
        alter.append( "INDEX " );

        if (this.getIndexName() != null) {
            alter.append(database.escapeIndexName(this.getCatalogName(), this.getSchemaName(), this.getIndexName()));
        }

        return alter.toString();
    }

    @Override
    public Boolean getUsePercona() {
        return usePercona;
    }

    public void setUsePercona(Boolean usePercona) {
        this.usePercona = usePercona;
    }

    @Override
    public String getChangeSkipName() {
        return NAME;
    }

    @Override
    public String getTargetTableName() {
        return getTableName();
    }
}

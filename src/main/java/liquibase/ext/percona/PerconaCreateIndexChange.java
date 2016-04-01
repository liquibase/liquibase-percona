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

import java.util.Iterator;

import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.core.CreateIndexChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

@DatabaseChange(name = PerconaCreateIndexChange.NAME,
    description = "Creates an index on an existing column or set of columns.",
    priority = PerconaCreateIndexChange.PRIORITY, appliesTo = "index")
public class PerconaCreateIndexChange extends CreateIndexChange
{
    public static final String NAME = "createIndex";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    @Override
    public SqlStatement[] generateStatements( Database database )
    {
        return PerconaChangeUtil.generateStatements(PerconaCreateIndexChange.NAME,
                    database,
                    super.generateStatements(database),
                    getTableName(),
                    generateAlterStatement(database));
    }

    private String generateAlterStatement( Database database )
    {
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
            if (column.getComputed() == null) {
                alter.append(database.escapeColumnName(this.getCatalogName(), this.getSchemaName(), this.getTableName(), column.getName(), false));
            } else {
                if (column.getComputed()) {
                    alter.append(column.getName());
                } else {
                    alter.append(database.escapeColumnName(this.getCatalogName(), this.getSchemaName(), this.getTableName(), column.getName()));
                }
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
}

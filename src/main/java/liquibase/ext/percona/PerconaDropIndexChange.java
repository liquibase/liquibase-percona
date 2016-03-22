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

@DatabaseChange(name="dropIndex",
    description = "Drops an existing index",
    priority = PerconaDropIndexChange.PRIORITY, appliesTo = "index")
public class PerconaDropIndexChange extends DropIndexChange {
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    @Override
    public SqlStatement[] generateStatements( Database database )
    {
        return PerconaChangeUtil.generateStatements(database,
                    super.generateStatements(database),
                    getTableName(),
                    generateAlterStatement(database));
    }

    private String generateAlterStatement( Database database )
    {
        StringBuilder alter = new StringBuilder();

        alter.append( "DROP ");
        alter.append( "INDEX " );

        if (this.getIndexName() != null) {
            alter.append(database.escapeIndexName(this.getCatalogName(), this.getSchemaName(), this.getIndexName()));
        }

        return alter.toString();
    }
}

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

import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;

public class PTOnlineSchemaChangeGenerator extends AbstractSqlGenerator<PTOnlineSchemaChangeStatement> {

    @Override
    public ValidationErrors validate(PTOnlineSchemaChangeStatement statement, Database database, SqlGeneratorChain<PTOnlineSchemaChangeStatement> sqlGeneratorChain) {
        return null;
    }

    @Override
    public Sql[] generateSql(PTOnlineSchemaChangeStatement statement, Database database, SqlGeneratorChain<PTOnlineSchemaChangeStatement> sqlGeneratorChain) {
        return new Sql[0];
    }
}

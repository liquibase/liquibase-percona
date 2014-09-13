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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;

/**
 * Subclasses the original {@link liquibase.change.core.DropColumnChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = "dropColumn", description = "Drop an existing column", priority = DropColumnChange.PRIORITY,
    appliesTo = "column")
public class DropColumnChange extends liquibase.change.core.DropColumnChange {
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private Logger log = LogFactory.getInstance().getLog();

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
        List<SqlStatement> statements = new ArrayList<SqlStatement>(Arrays.asList(super.generateStatements(database)));

        if (database instanceof MySQLDatabase) {
            if (PTOnlineSchemaChangeStatement.isAvailable()) {
                PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement(
                        getTableName(),
                        generateAlterStatement());

                if (ChangeUtil.isDryRun(database)) {
                    CommentStatement commentStatement = new CommentStatement(statement.printCommand(database));

                    if (Configuration.noAlterSqlDryMode()) {
                        statements.clear();
                        statements.add(0, commentStatement);
                    } else {
                        statements.add(0, commentStatement);
                        statements.add(1, new CommentStatement("Instead of the following statements, pt-online-schema-change will be used"));
                    }
                } else {
                    statements.clear();
                    statements.add(statement);
                }
            } else {
                if (Configuration.failIfNoPT()) {
                    throw new RuntimeException("No percona toolkit found!");
                }
                log.warning("Not using percona toolkit, because it is not available!");
            }
        }

        return statements.toArray(new SqlStatement[statements.size()]);
    }

    String generateAlterStatement() {
        StringBuilder alter = new StringBuilder();
        alter.append("DROP COLUMN ").append(getColumnName());
        return alter.toString();
    }
}

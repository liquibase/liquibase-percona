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

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.RawSQLChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

@DatabaseChange(
        name = PerconaRawSQLChange.NAME,
        description = "The 'sql' tag allows you to specify whatever sql you want. It is useful for complex changes that aren't supported through Liquibase's automated refactoring tags and to work around bugs and limitations of Liquibase. The SQL contained in the sql tag can be multi-line.\n\nThe createProcedure refactoring is the best way to create stored procedures.\n\nThe 'sql' tag can also support multiline statements in the same file. Statements can either be split using a ; at the end of the last line of the SQL or a 'GO' on its own on the line between the statements can be used. Multiline SQL statements are also supported and only a ; or GO statement will finish a statement, a new line is not enough. Files containing a single statement do not need to use a ; or GO.\n\nThe sql change can also contain comments of either of the following formats:\n\nA multiline comment that starts with /* and ends with */.\nA single line comment starting with <space>--<space> and finishing at the end of the line.\nNote: By default it will attempt to split statements on a ';' or 'go' at the end of lines. Because of this, if you have a comment or some other non-statement ending ';' or 'go', don't have it at the end of a line or you will get invalid SQL.",
        priority = PerconaRawSQLChange.PRIORITY
)
public class PerconaRawSQLChange extends RawSQLChange implements PerconaChange {
    public static final String NAME = "sql";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private static final String ALTER_TABLE_PREFIX = "alter table ";

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return PerconaChangeUtil.generateStatements(this,
                database,
                super.generateStatements(database));
    }

    @Override
    public String generateAlterStatement(Database database) {
        String sql = getSql();
        if (sql == null) {
            return null;
        }

        String tableName = getTargetTableName();
        return sql.substring(sql.indexOf(tableName) + tableName.length() + 1);
    }

    @Override
    public String getTargetDatabaseName() {
        // will fall back to Database#getLiquibaseCatalogName(), see PTOnlineSchemaChangeStatement
        return null;
    }

    @Override
    public String getTargetTableName() {
        String sql = getSql();
        if (sql == null) {
            return null;
        }

        String lowerCaseSql = sql.toLowerCase(Locale.ROOT);
        if (lowerCaseSql.trim().startsWith(ALTER_TABLE_PREFIX)) {
            String table = sql.substring(sql.toLowerCase(Locale.ROOT).indexOf(ALTER_TABLE_PREFIX) + ALTER_TABLE_PREFIX.length());
            table = table.substring(0, table.indexOf(' '));
            return table;
        }
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
    //CPD-ON
}

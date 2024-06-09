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
import java.util.Set;

import liquibase.Scope;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.RawSQLChange;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.util.StringUtil;

@DatabaseChange(
        name = PerconaRawSQLChange.NAME,
        description = "The 'sql' tag allows you to specify whatever sql you want. It is useful for complex changes that aren't supported through Liquibase's automated refactoring tags and to work around bugs and limitations of Liquibase. The SQL contained in the sql tag can be multi-line.\n\nThe createProcedure refactoring is the best way to create stored procedures.\n\nThe 'sql' tag can also support multiline statements in the same file. Statements can either be split using a ; at the end of the last line of the SQL or a 'GO' on its own on the line between the statements can be used. Multiline SQL statements are also supported and only a ; or GO statement will finish a statement, a new line is not enough. Files containing a single statement do not need to use a ; or GO.\n\nThe sql change can also contain comments of either of the following formats:\n\nA multiline comment that starts with /* and ends with */.\nA single line comment starting with <space>--<space> and finishing at the end of the line.\nNote: By default it will attempt to split statements on a ';' or 'go' at the end of lines. Because of this, if you have a comment or some other non-statement ending ';' or 'go', don't have it at the end of a line or you will get invalid SQL.",
        priority = PerconaRawSQLChange.PRIORITY
)
public class PerconaRawSQLChange extends RawSQLChange implements PerconaChange {
    public static final String NAME = "sql";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private static Logger log = Scope.getCurrentScope().getLog(PerconaRawSQLChange.class);

    private boolean determinedTargetNames;
    private String targetDatabaseName;
    private String targetTableName;

    @Override
    public SqlStatement[] generateStatements(Database database) {
        String table = getTargetTableName();
        if (table == null) {
            // can't use percona toolkit for whatever reason
            return super.generateStatements(database);
        }

        return PerconaChangeUtil.generateStatements(this,
                database,
                super.generateStatements(database));
    }

    /**
     * Extracts the alter options from the sql statement. If the sql statement is not an
     * alter table statement, then there are no alter options to execute and this method returns
     * {@code null}.
     *
     * @param database the database connection
     * @return the alter options to be passed to pt-osc or {@code null} if pt-osc can't be used.
     */
    @Override
    public String generateAlterStatement(Database database) {
        String sql = getSql();
        if (sql == null) {
            return null;
        }

        String tableName = getTargetTableName();
        if (tableName == null) {
            return null;
        }

        String[] multiLineSQL = StringUtil.processMultiLineSQL(sql, true, true, getEndDelimiter());
        assert multiLineSQL.length == 1;

        String alterOptions = multiLineSQL[0].trim();
        alterOptions = alterOptions.substring(alterOptions.indexOf(tableName) + tableName.length() + 1);
        return alterOptions.trim();
    }

    @Override
    public String getTargetDatabaseName() {
        determineTargetNames();
        return targetDatabaseName;
    }

    /**
     * Returns the previously determined table name from the sql statements or {@code null} if the
     * sql statement could not be parsed. Then pt-osc won't be used.
     *
     * @return the table name or {@code null} if the sql statement couldn't be parsed.
     */
    @Override
    public String getTargetTableName() {
        determineTargetNames();
        return targetTableName;
    }

    @Override
    public void setSql(String sql) {
        super.setSql(sql);
        determinedTargetNames = false;
    }

    /**
     * Tries to determine the database and table names from the sql statements. This is only possible, if the statement
     * begins with "alter table".
     * <p>In case, the table name could not be determined, this will unset targetDatabaseName and targetTableName.
     * A following call to {@link #getTargetTableName()} will return {@code null} then. That means,
     * that we can't use pt-osc.</p>
     */
    private void determineTargetNames() {
        if (determinedTargetNames) {
            return;
        }

        targetDatabaseName = null;
        targetTableName = null;
        determinedTargetNames = true;

        String sql = getSql();
        if (sql == null) {
            return;
        }
        if (Boolean.FALSE.equals(getUsePercona())) {
            // avoids unnecessary warnings
            return;
        }

        String[] multiLineSQL = StringUtil.processMultiLineSQL(sql, true, true, getEndDelimiter());
        if (multiLineSQL.length != 1) {
            log.warning("Not using percona toolkit, because multiple statements are not supported: " + sql);
            return;
        }

        AlterTableParser parser = new AlterTableParser(multiLineSQL[0]);
        if (!parser.isValid()) {
            log.warning("Not using percona toolkit, because this sql statement is not an alter table: " + sql);
            return;
        }

        // rename table?
        String[] tokens = parser.getAlterTableOptions().split("\\s+");
        if (tokens.length >= 2 && tokens[0].equalsIgnoreCase("rename")
            && !tokens[1].equalsIgnoreCase("column")
            && !tokens[1].equalsIgnoreCase("index")
            && !tokens[1].equalsIgnoreCase("key")) {
            log.warning("Not using percona toolkit, because can't rename table: " + sql);
            return;
        }

        targetDatabaseName = parser.getTargetDatabaseName();
        targetTableName = parser.getTargetTableName();
        log.fine("Determined target database: " + targetDatabaseName);
        log.fine("Determined target table: " + targetTableName);
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
    //CPD-ON
}

package liquibase.ext.percona;

import java.util.Collections;
import java.util.List;

import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AddUniqueConstraintChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.util.StringUtils;

/**
 * Subclasses the original {@link liquibase.change.core.AddForeignKeyConstraintChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = PerconaAddUniqueConstraintChange.NAME, description = "Adds a unique constrant to an existing column or set of columns",
    priority = PerconaAddUniqueConstraintChange.PRIORITY, appliesTo = "table")
public class PerconaAddUniqueConstraintChange extends AddUniqueConstraintChange {
    public static final String NAME = "addUniqueConstraint";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    /**
     * Generates the statements required for the add unique constraint change.
     * In case of a MySQL database, percona toolkit will be used.
     * In case of generating the SQL statements for review (updateSQL) the command
     * will be added as a comment.
     * @param database the database
     * @return the list of statements
     * @see PTOnlineSchemaChangeStatement
     */
    @Override
    public SqlStatement[] generateStatements(Database database) {
        return PerconaChangeUtil.generateStatements(PerconaAddUniqueConstraintChange.NAME,
                database,
                super.generateStatements(database),
                getTableName(),
                generateAlterStatement(database));
    }

    String generateAlterStatement(Database database) {
        StringBuilder alter = new StringBuilder();

        alter.append("ADD ");
        if (StringUtil.isNotEmpty(getConstraintName())) {
            alter.append("CONSTRAINT ");
            alter.append(database.escapeConstraintName(getConstraintName()));
            alter.append(" ");
        }
        alter.append("UNIQUE (");
        List<String> columns = StringUtils.splitAndTrim(getColumnNames(), ",");
        if (columns == null) columns = Collections.emptyList();
        alter.append(database.escapeColumnNameList(StringUtils.join(columns, ", ")));
        alter.append(")");

        return alter.toString();
    }

    @Override
    protected Change[] createInverses() {
        // that's the percona drop unique constraint change
        PerconaDropUniqueConstraintChange inverse = new PerconaDropUniqueConstraintChange();
        inverse.setSchemaName(getSchemaName());
        inverse.setTableName(getTableName());
        inverse.setConstraintName(getConstraintName());

        return new Change[] { inverse };
    }
}

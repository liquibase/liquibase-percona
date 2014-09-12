package liquibase.ext.percona;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import liquibase.change.AddColumnConfig;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.datatype.DataTypeFactory;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;

/**
 * Subclasses the original {@link liquibase.change.core.AddColumnChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name="addColumn", description = "Adds a new column to an existing table",
    priority = AddColumnChange.PRIORITY, appliesTo = "table")
public class AddColumnChange extends liquibase.change.core.AddColumnChange {

    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private Logger log = LogFactory.getInstance().getLog();

    /**
     * Generates the statements required for the add column change.
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
                log.info("Using percona toolkit: " + PTOnlineSchemaChangeStatement.getVersion());
                PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement(
                        getTableName(),
                        generateAlterStatement(database));

                if (isDryRun(database)) {
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

    /**
     * Determines whether *SQL (updateSQL/rollbackSQL) is executed or whether
     * the statements should be executed directly.
     * @param database the database
     * @return <code>true</code> if dry-run is enabled and the statements should *not* be executed.
     */
    private boolean isDryRun(Database database) {
        Executor executor = ExecutorService.getInstance().getExecutor(database);
        if (executor instanceof LoggingExecutor) {
            return true;
        }
        return false;
    }

    String generateAlterStatement(Database database) {
        StringBuilder alter = new StringBuilder();
        boolean firstColumn = true;
        for (AddColumnConfig a : getColumns()) {
            if (!firstColumn) {
                alter.append(", ");
            }
            alter.append(convertColumnToSql(a, database));
            firstColumn = false;
        }
        return alter.toString();
    }

    String convertColumnToSql(AddColumnConfig column, Database database) {
        String nullable = "";
        if (column.getConstraints() != null && !column.getConstraints().isNullable()) {
            nullable = " NOT NULL";
        } else {
            nullable = " NULL";
        }
        String defaultValue =  "";
        if (column.getDefaultValueObject() != null) {
            defaultValue = " DEFAULT " + DataTypeFactory.getInstance().fromObject(column.getDefaultValueObject(), database).objectToSql(column.getDefaultValueObject(), database);
        }
        String comment = "";
        if (column.getRemarks() != null) {
            comment += " COMMENT '" + column.getRemarks() + "'";
        }
        return "ADD COLUMN " + database.escapeColumnName(null, null, null, column.getName())
                + " " + DataTypeFactory.getInstance().fromDescription(column.getType(), database).toDatabaseDataType(database)
                + nullable
                + defaultValue
                + comment;
    }
}

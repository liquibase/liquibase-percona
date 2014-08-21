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
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CommentStatement;

/**
 * @author Andreas Dangel
 *
 */
@DatabaseChange(name="addColumn", description = "Adds a new column to an existing table",
    priority = AddColumnChange.PRIORITY, appliesTo = "table")
public class AddColumnChange extends liquibase.change.core.AddColumnChange {

    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    @Override
    public SqlStatement[] generateStatements(Database database) {
        List<SqlStatement> statements = new ArrayList<SqlStatement>(Arrays.asList(super.generateStatements(database)));

        if (database instanceof MySQLDatabase) {
            if (PTOnlineSchemaChangeStatement.isAvailable()) {
                PTOnlineSchemaChangeStatement statement = new PTOnlineSchemaChangeStatement(
                        getTableName(),
                        generateAlterStatement(database));

                if (isDryRun(database)) {
                    statements.add(0, new CommentStatement("Instead of the following statements, pt-online-schema-change will be used"));
                    statements.add(1, new CommentStatement(statement.printCommand(database)));
                } else {
                    statements.clear();
                    statements.add(statement);
                }
            } else {
                System.out.println("Warning: Not using percona toolkit!");
            }
        }

        return statements.toArray(new SqlStatement[statements.size()]);
    }

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
            alter.append(convertToSql(a, database));
            firstColumn = false;
        }
        return alter.toString();
    }

    String convertToSql(AddColumnConfig column, Database database) {
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

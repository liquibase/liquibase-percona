package liquibase.ext.percona;

import java.util.Collections;
import java.util.List;

import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AddForeignKeyConstraintChange;
import liquibase.database.Database;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.util.StringUtils;

/**
 * Subclasses the original {@link liquibase.change.core.AddForeignKeyConstraintChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = PerconaAddForeignKeyConstraintChange.NAME, description = "Adds a foreign key constraint to an existing column",
    priority = PerconaAddForeignKeyConstraintChange.PRIORITY, appliesTo = "table")
public class PerconaAddForeignKeyConstraintChange extends AddForeignKeyConstraintChange {
    public static final String NAME = "addForeignKeyConstraint";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    /**
     * Generates the statements required for the add foreign key constraint change.
     * In case of a MySQL database, percona toolkit will be used.
     * In case of generating the SQL statements for review (updateSQL) the command
     * will be added as a comment.
     * @param database the database
     * @return the list of statements
     * @see PTOnlineSchemaChangeStatement
     */
    @Override
    public SqlStatement[] generateStatements(Database database) {
        return PerconaChangeUtil.generateStatements(PerconaAddForeignKeyConstraintChange.NAME,
                database,
                super.generateStatements(database),
                getBaseTableName(),
                generateAlterStatement(database));
    }

    String generateAlterStatement(Database database) {
        StringBuilder alter = new StringBuilder();

        alter.append("ADD CONSTRAINT ");
        if (StringUtil.isNotEmpty(getConstraintName())) {
            alter.append(database.escapeConstraintName(getConstraintName())).append(" ");
        }
        alter.append("FOREIGN KEY ");

        alter.append("(");
        List<String> baseColumns = StringUtils.splitAndTrim(getBaseColumnNames(), ",");
        if (baseColumns == null) baseColumns = Collections.emptyList();
        alter.append(database.escapeColumnNameList(StringUtils.join(baseColumns, ", ")));
        alter.append(") ");

        alter.append("REFERENCES ");
        alter.append(resolveReferencedPerconaTableName(database)).append(" ");
        alter.append("(");
        List<String> referencedColumns = StringUtils.splitAndTrim(getReferencedColumnNames(), ",");
        if (referencedColumns == null) referencedColumns = Collections.emptyList();
        alter.append(database.escapeColumnNameList(StringUtils.join(referencedColumns, ", ")));
        alter.append(")");

        if (getOnDelete() != null) {
            alter.append(" ON DELETE ").append(getOnDelete());
        }
        if (getOnUpdate() != null) {
            alter.append(" ON UPDATE ").append(getOnUpdate());
        }

        if (getDeferrable() != null && getDeferrable()) {
            alter.append(" DEFERRABLE");
        }
        if (getInitiallyDeferred() != null && getInitiallyDeferred()) {
            alter.append(" INITIALLY DEFERRED");
        }

        return alter.toString();
    }

    @Override
    protected Change[] createInverses() {
        // that's the percona drop foreign key constraint change
        PerconaDropForeignKeyConstraintChange inverse = new PerconaDropForeignKeyConstraintChange();
        inverse.setBaseTableSchemaName(getBaseTableSchemaName());
        inverse.setBaseTableName(getBaseTableName());
        inverse.setConstraintName(getConstraintName());

        return new Change[] { inverse };
    }

    /**
     * In case the foreign key is self-referencing the table itself, we have to deal with this
     * the temporary percona table name. Since percona copies the old table and performas the alters
     * on the copy, we need to reference the copy in that case ("_new" suffix).
     *
     * Since this bug is scheduled to be fixed with pt 2.2.21, the workaround will be applied
     * only for earlier versions.
     *
     * @param database
     * @return
     *
     * @see <a href="https://bugs.launchpad.net/percona-toolkit/+bug/1393961">pt-online-schema-change fails with self-referential foreign key</a>
     */
    private String resolveReferencedPerconaTableName(Database database) {
        if (getBaseTableName() != null && getBaseTableName().equals(getReferencedTableName())
                && !PTOnlineSchemaChangeStatement.getVersion().isGreaterOrEqualThan("2.2.21")) {
            Logger log = LogFactory.getInstance().getLog();
            log.warning("Applying workaround for pt-osc bug https://bugs.launchpad.net/percona-toolkit/+bug/1393961");
            return database.escapeTableName(getReferencedTableCatalogName(), getReferencedTableSchemaName(), "_" + getReferencedTableName() + "_new");
        }

        return database.escapeTableName(getReferencedTableCatalogName(), getReferencedTableSchemaName(), getReferencedTableName());
    }
}

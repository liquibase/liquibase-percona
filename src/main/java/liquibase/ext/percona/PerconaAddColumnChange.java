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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.DropDefaultValueChange;
import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.statement.SqlStatement;

/**
 * Subclasses the original {@link liquibase.change.core.AddColumnChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = PerconaAddColumnChange.NAME, description = "Adds a new column to an existing table",
    priority = PerconaAddColumnChange.PRIORITY, appliesTo = "table")
public class PerconaAddColumnChange extends AddColumnChange implements PerconaChange {
    public static final String NAME = "addColumn";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

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
        return PerconaChangeUtil.generateStatements(this,
                database,
                super.generateStatements(database));
    }

    @Override
    public String generateAlterStatement(Database database) {
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
        ConstraintsConfig constraintsConfig = column.getConstraints();
        if (constraintsConfig != null && constraintsConfig.isNullable() != null && !constraintsConfig.isNullable()) {
            nullable = " NOT NULL";
        } else {
            nullable = " NULL";
        }
        String defaultValue =  "";
        if (column.getDefaultValueObject() != null) {
            defaultValue = " DEFAULT " + DataTypeFactory.getInstance().fromObject(column.getDefaultValueObject(), database).objectToSql(column.getDefaultValueObject(), database);
        }
        String comment = "";
        if (StringUtil.isNotEmpty(column.getRemarks())) {
            comment += " COMMENT '" + column.getRemarks() + "'";
        }
        String after = "";
        if (StringUtil.isNotEmpty(column.getAfterColumn())) {
            after += " AFTER " + database.escapeColumnName(null, null, null, column.getAfterColumn());
        }

        String constraints = "";
        constraints += addForeignKeyConstraint(column, database);
        constraints += addUniqueKeyConstraint(column, database);

        return "ADD COLUMN " + database.escapeColumnName(null, null, null, column.getName())
                + " " + DataTypeFactory.getInstance().fromDescription(column.getType(), database).toDatabaseDataType(database)
                + nullable
                + defaultValue
                + comment
                + after
                + constraints;
    }

    private String addForeignKeyConstraint(AddColumnConfig column, Database database) {
        String result = "";
        ConstraintsConfig constraintsConfig = column.getConstraints();
        if (constraintsConfig != null && (StringUtil.isNotEmpty(constraintsConfig.getReferences()) || StringUtil.isNotEmpty(constraintsConfig.getReferencedTableName()) )) {
            result += ", ADD ";
            if (StringUtil.isNotEmpty(constraintsConfig.getForeignKeyName())) {
                result += "CONSTRAINT " + database.escapeConstraintName(constraintsConfig.getForeignKeyName()) + " ";
            }
            result +=  "FOREIGN KEY ("
                    + database.escapeColumnName(null, null, null, column.getName()) + ") REFERENCES ";

            String referencedTable;
            String referencedColumn;

            if (StringUtil.isNotEmpty(constraintsConfig.getReferences())) {
                Matcher references = Pattern.compile("([\\w\\._]+)\\(([\\w_]+)\\)").matcher(constraintsConfig.getReferences());
                if (!references.matches()) {
                    throw new UnexpectedLiquibaseException("Unable to get table name and column name from " + constraintsConfig.getReferences());
                }
                referencedTable = references.group(1);
                referencedColumn = references.group(2);
            } else {
                referencedTable = constraintsConfig.getReferencedTableName();
                referencedColumn = constraintsConfig.getReferencedColumnNames();
            }

            referencedTable = PerconaChangeUtil.resolveReferencedPerconaTableName(getTableName(), referencedTable);

            result += database.escapeTableName(null, null, referencedTable) + "(";
            result += database.escapeColumnName(null, null, null, referencedColumn);
            result += ")";
        }
        return result;
    }

    private String addUniqueKeyConstraint(AddColumnConfig column, Database database) {
        String result = "";
        ConstraintsConfig constraintsConfig = column.getConstraints();
        if (constraintsConfig != null && constraintsConfig.isUnique() != null && constraintsConfig.isUnique()) {
            result += ", ADD ";
            if (StringUtil.isNotEmpty(constraintsConfig.getUniqueConstraintName())) {
                result += "CONSTRAINT " + database.escapeConstraintName(constraintsConfig.getUniqueConstraintName()) + " ";
            }
            result +=  "UNIQUE (" + database.escapeColumnName(null, null, null, column.getName()) + ")";
        }
        return result;
    }

    @Override
    protected Change[] createInverses() {
        List<Change> inverses = new ArrayList<Change>();

        for (ColumnConfig aColumn : getColumns()) {
            if (aColumn.hasDefaultValue()) {
                DropDefaultValueChange dropChange = new DropDefaultValueChange();
                dropChange.setTableName(getTableName());
                dropChange.setColumnName(aColumn.getName());
                dropChange.setSchemaName(getSchemaName());
                dropChange.setCatalogName(getCatalogName());
                inverses.add(dropChange);
            }

            // that's the percona drop column change.
            PerconaDropColumnChange inverse = new PerconaDropColumnChange();
            inverse.setSchemaName(getSchemaName());
            inverse.setColumnName(aColumn.getName());
            inverse.setCatalogName(getCatalogName());
            inverse.setTableName(getTableName());
            inverses.add(inverse);
        }

        return inverses.toArray(new Change[inverses.size()]);
    }

    @Override
    public String getTargetTableName() {
        return getTableName();
    }

    @Override
    public String getTargetDatabaseName() {
        return getCatalogName();
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

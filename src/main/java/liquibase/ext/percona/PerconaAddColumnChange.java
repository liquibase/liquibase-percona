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
import java.util.List;

import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.DropDefaultValueChange;
import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.statement.SqlStatement;

/**
 * Subclasses the original {@link liquibase.change.core.AddColumnChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = PerconaAddColumnChange.NAME, description = "Adds a new column to an existing table",
    priority = PerconaAddColumnChange.PRIORITY, appliesTo = "table")
public class PerconaAddColumnChange extends AddColumnChange {
    public static final String NAME= "addColumn";
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
        return PerconaChangeUtil.generateStatements(PerconaAddColumnChange.NAME,
                database,
                super.generateStatements(database),
                getTableName(),
                generateAlterStatement(database));
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

    /**
     * Simple isNotEmpty check, avoiding NPE.
     * Note: not use StringUtil.isNotEmpty, since this method/class doesn't exist
     * for liquibase 3.3.x and 3.4.x.
     * @param s the string to test
     * @return <code>true</code> if s is not null and not empty, <code>false</code> otherwise.
     */
    private static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    String convertColumnToSql(AddColumnConfig column, Database database) {
        String nullable = "";
        ConstraintsConfig constraintsConfig = column.getConstraints();
        if (constraintsConfig != null && !constraintsConfig.isNullable()) {
            nullable = " NOT NULL";
        } else {
            nullable = " NULL";
        }
        String defaultValue =  "";
        if (column.getDefaultValueObject() != null) {
            defaultValue = " DEFAULT " + DataTypeFactory.getInstance().fromObject(column.getDefaultValueObject(), database).objectToSql(column.getDefaultValueObject(), database);
        }
        String comment = "";
        if (isNotEmpty(column.getRemarks())) {
            comment += " COMMENT '" + column.getRemarks() + "'";
        }
        String after = "";
        if (isNotEmpty(column.getAfterColumn())) {
            after += " AFTER " + database.escapeColumnName(null, null, null, column.getAfterColumn());
        }

        String constraints = "";
        if (constraintsConfig != null && isNotEmpty(constraintsConfig.getReferences())) {
            constraints += ", ADD ";
            if (isNotEmpty(constraintsConfig.getForeignKeyName())) {
                constraints += "CONSTRAINT " + constraintsConfig.getForeignKeyName() + " ";
            }
            constraints +=  "FOREIGN KEY ("
                    + database.escapeColumnName(null, null, null, column.getName()) + ") REFERENCES "
                    + constraintsConfig.getReferences();
        }
        if (constraintsConfig != null && constraintsConfig.isUnique() != null && constraintsConfig.isUnique()) {
            constraints += ", ADD ";
            if (isNotEmpty(constraintsConfig.getUniqueConstraintName())) {
                constraints += "CONSTRAINT " + constraintsConfig.getUniqueConstraintName() + " ";
            }
            constraints +=  "UNIQUE (" + database.escapeColumnName(null, null, null, column.getName()) + ")";
        }

        return "ADD COLUMN " + database.escapeColumnName(null, null, null, column.getName())
                + " " + DataTypeFactory.getInstance().fromDescription(column.getType(), database).toDatabaseDataType(database)
                + nullable
                + defaultValue
                + comment
                + after
                + constraints;
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
                inverses.add(dropChange);
            }

            // that's the percona drop column change.
            PerconaDropColumnChange inverse = new PerconaDropColumnChange();
            inverse.setSchemaName(getSchemaName());
            inverse.setColumnName(aColumn.getName());
            inverse.setTableName(getTableName());
            inverses.add(inverse);
        }

        return inverses.toArray(new Change[inverses.size()]);
    }

}

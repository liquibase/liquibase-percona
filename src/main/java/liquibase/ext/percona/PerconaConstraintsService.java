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
import java.util.List;

import liquibase.CatalogAndSchema;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.structure.core.ForeignKey;
import liquibase.structure.core.PrimaryKey;
import liquibase.structure.core.Table;

public class PerconaConstraintsService {
    private static PerconaConstraintsService instance = new PerconaConstraintsService();
    private Logger log = LogFactory.getInstance().getLog();
    private boolean enabled = true;

    public static PerconaConstraintsService getInstance() {
        return instance;
    }

    /**
     * The functionality can be disabled - this is especially useful for unit tests,
     * where there is no real database to query.
     */
    public void disable() {
        if (enabled) {
            log.warning("Disabled dynamic foreign key name resolution - DropForeignKeyConstraint might not work with pt-osc");
            enabled = false;
        }
    }

    /**
     * pt-osc changes the constraint names to avoid collisions.
     * It prefixes the original name with 0, 1 or 2 underscores.
     *
     * @param database the database
     * @param change the foreign key constraint change
     * @return the prefixed constraint name
     */
    public String determineCurrentConstraintName(Database database, PerconaDropForeignKeyConstraintChange change) {
        // start with best guess without looking at the database
        String constraintName = change.getConstraintName();

        if (enabled && !PerconaChangeUtil.isDryRun(database) && PerconaChangeUtil.isConnected(database)) {
            constraintName = findForeignKey(database, change);
        }
        return prefixConstraintName(constraintName);
    }

    private String findForeignKey(Database database, PerconaDropForeignKeyConstraintChange change) {
        log.debug("Searching for all foreign keys in table " + change.getBaseTableName());

        try {
            SnapshotControl control = new SnapshotControl(database);
            control.getTypesToInclude().add(ForeignKey.class);
            CatalogAndSchema catalogAndSchema = new CatalogAndSchema(change.getBaseTableCatalogName(), change.getBaseTableSchemaName())
                    .standardize(database);
            Table target = SnapshotGeneratorFactory.getInstance().createSnapshot(new Table(catalogAndSchema.getCatalogName(), catalogAndSchema.getSchemaName(),
                    database.correctObjectName(change.getBaseTableName(), Table.class)), database);

            List<ForeignKey> results = (target == null) ? Collections.<ForeignKey> emptyList() : target.getOutgoingForeignKeys();
            for (ForeignKey fk : results) {
                Table baseTable = fk.getForeignKeyTable();
                String constraintName = fk.getName();
                log.debug("Found FK: " + baseTable.getName() + "." + constraintName);

                if (baseTable.getName().equalsIgnoreCase(change.getBaseTableName())
                    && constraintName.endsWith(change.getConstraintName())) {
                        log.debug("Found current foreign key constraint " + constraintName);
                        return constraintName;
                }
            }
        } catch (DatabaseException e) {
            throw new UnexpectedLiquibaseException("Failed to find foreign keys for table: " + change.getBaseTableName(), e);
        } catch (InvalidExampleException e) {
            throw new UnexpectedLiquibaseException("Failed to find foreign keys for table: " + change.getBaseTableName(), e);
        }

        log.warning("No foreign key with name " + change.getConstraintName() + " found.");
        return change.getConstraintName();
    }

    /**
     * We use the same algorithm as pt-osc.
     *
     * <blockquote>
     * So we do replacements when constraint names:<br>
     * Has 2 _, we remove them<br>
     * Has 1 _, we add one to make 2<br>
     * Has no _, we add one to make 1<br>
     * </blockquote>
     * See <a href="https://github.com/percona/percona-toolkit/blob/5498a4da8170e782ed0b293555a08730e1d83f38/bin/pt-online-schema-change#L10448-L10457">
     * pt-osc lines 10448ff.</a>
     *
     * @param constraintName
     * @return
     */
    private String prefixConstraintName(String constraintName) {
        if (constraintName != null && constraintName.startsWith("__")) {
            return constraintName.substring(2);
        } else {
            return "_" + constraintName;
        }
    }

    /**
     * Checks whether the table, to which a primary key should be added, has already a primary key.
     *
     * <p>Note: This needs database access.
     *
     * @param database the database
     * @param change the add primary key change
     * @return <code>true</code> if the table has already a primary key, <code>false</code> otherwise.
     */
    public boolean hasPrimaryKey(Database database, PerconaAddPrimaryKeyChange change) {
        boolean result = false;

        if (enabled && !PerconaChangeUtil.isDryRun(database) && PerconaChangeUtil.isConnected(database)) {
            log.debug("Searching for primary key in table " + change.getTableName());

            try {
                PrimaryKey primaryKeyExample = new PrimaryKey("primary", change.getCatalogName(), change.getSchemaName(), change.getTableName());
                result = SnapshotGeneratorFactory.getInstance().has(primaryKeyExample, database);
            } catch (DatabaseException e) {
                // this might happen, if the table does not exist yet.
                // the primary key is checked already during changelog validation before any change might have been executed.
                log.debug("Failed to find primary key for table: " + change.getTableName(), e);
            } catch (InvalidExampleException e) {
                throw new UnexpectedLiquibaseException("Failed to find primary key for table: " + change.getTableName(), e);
            }

            log.debug("No primary key in table " + change.getTableName() + " found.");
        }
        return result;
    }
}

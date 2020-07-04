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

import java.io.StringWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import liquibase.change.Change;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;
import liquibase.exception.RollbackImpossibleException;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.executor.jvm.JdbcExecutor;
import liquibase.statement.SqlStatement;

@ExtendWith(RestoreSystemPropertiesExtension.class)
public abstract class AbstractPerconaChangeTest<T extends Change> {

    private Database database;
    private T change;
    private final Class<T> changeClazz;
    private String targetTableName = "person";
    private String targetDatabaseName = "testdb";

    public AbstractPerconaChangeTest(Class<T> clazz) {
        changeClazz = clazz;

        try {
            change = changeClazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        System.setProperty(Configuration.LIQUIBASE_PASSWORD, "root");

        database = new MySQLDatabase();
        database.setLiquibaseSchemaName("testdb");
        DatabaseConnection conn = new MockDatabaseConnection("jdbc:mysql://user@localhost:3306/testdb",
                "user@localhost");
        database.setConnection(conn);
        JdbcExecutor executor = new JdbcExecutor();
        executor.setDatabase(database);
        ExecutorService.getInstance().setExecutor(database, executor);

        PTOnlineSchemaChangeStatement.available = true;
        PTOnlineSchemaChangeStatement.perconaToolkitVersion = null;
        System.setProperty(Configuration.FAIL_IF_NO_PT, "false");
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "false");
        System.setProperty(Configuration.SKIP_CHANGES, "");

        PerconaConstraintsService.getInstance().disable();

        setupChange(change);
    }

    protected abstract void setupChange(T change);

    protected void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    protected void setTargetDatabaseName(String targetDatabaeName) {
        this.targetDatabaseName = targetDatabaeName;
    }

    protected T getChange() {
        return change;
    }
    protected Database getDatabase() {
        return database;
    }

    protected void assertPerconaRollbackChange(String alter) throws RollbackImpossibleException {
        assertPerconaChange(alter, generateRollbackStatements());
    }
    protected void assertPerconaChange(String alter) {
        assertPerconaChange(alter, generateStatements());
    }

    protected void assertPerconaChange(String alter, SqlStatement[] statements) {
        Assertions.assertEquals(1, statements.length);
        Assertions.assertEquals(PTOnlineSchemaChangeStatement.class, statements[0].getClass());
        Assertions.assertEquals("pt-online-schema-change "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--alter=\"" + alter + "\" "
                + "--password=*** --execute "
                + "h=localhost,P=3306,u=user,D=" + targetDatabaseName + ",t=" + targetTableName,
                ((PTOnlineSchemaChangeStatement)statements[0]).printCommand(database));
    }

    protected SqlStatement[] generateStatements() {
        return change.generateStatements(database);
    }
    protected SqlStatement[] generateRollbackStatements() throws RollbackImpossibleException {
        return change.generateRollbackStatements(database);
    }

    protected void enableLogging() {
        ExecutorService.getInstance().setExecutor(database, new LoggingExecutor(null, new StringWriter(), database));
    }

    @Test
    public void testUnitializedChange() throws Exception {
        change = changeClazz.getConstructor().newInstance();
        change.generateStatements(database);
    }
}

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

import liquibase.change.Change;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;
import liquibase.exception.RollbackImpossibleException;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.executor.jvm.JdbcExecutor;
import liquibase.statement.SqlStatement;

public abstract class AbstractPerconaChangeTest<T extends Change> {

    private Database database;
    private T change;
    private final Class<T> changeClazz;
    private String targetTableName = "person";
    private String targetDatabaseName = "testdb";

    public AbstractPerconaChangeTest(Class<T> clazz) {
        changeClazz = clazz;

        try {
            change = changeClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Before
    public void setup() {
        System.setProperty(Configuration.LIQUIBASE_PASSWORD, "root");

        database = new MySQLDatabase();
        database.setLiquibaseSchemaName("testdb");
        DatabaseConnection conn = new MockDatabaseConnection("jdbc:mysql://user@localhost:3306/testdb",
                "user@localhost");
        database.setConnection(conn);
        ExecutorService.getInstance().setExecutor(database, new JdbcExecutor());

        PTOnlineSchemaChangeStatement.available = true;
        PTOnlineSchemaChangeStatement.perconaToolkitVersion = null;
        System.setProperty(Configuration.FAIL_IF_NO_PT, "false");
        System.setProperty(Configuration.NO_ALTER_SQL_DRY_MODE, "false");
        System.setProperty(Configuration.SKIP_CHANGES, "");

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
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals(PTOnlineSchemaChangeStatement.class, statements[0].getClass());
        Assert.assertEquals("pt-online-schema-change --alter=\"" + alter + "\" "
                + "--alter-foreign-keys-method=auto "
                + "--nocheck-unique-key-change "
                + "--host=localhost --port=3306 --user=user --password=*** --execute D=" + targetDatabaseName
                + ",t=" + targetTableName,
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
        change = changeClazz.newInstance();
        change.generateStatements(database);
    }
}

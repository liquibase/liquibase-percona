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
import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import liquibase.database.jvm.JdbcConnection;

public class DatabaseConnectionUtilTest {
    @Rule
    public RestoreSystemProperties restoreSystemPropertiesRule = new RestoreSystemProperties();

    @Test
    public void testGetHost() {
        DatabaseConnectionUtil util;

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3306/testdb"));
        Assert.assertEquals("localhost", util.getHost());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://localhost:3306/testdb"));
        Assert.assertEquals("localhost", util.getHost());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://127.0.0.1:3306/testdb"));
        Assert.assertEquals("127.0.0.1", util.getHost());
    }

    @Test
    public void testGetPort() {
        DatabaseConnectionUtil util;

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3307/testdb"));
        Assert.assertEquals("3307", util.getPort());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://localhost:3307/testdb"));
        Assert.assertEquals("3307", util.getPort());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://localhost/testdb"));
        Assert.assertEquals("3306", util.getPort());
    }

    @Test
    public void testGetUser() {
        DatabaseConnectionUtil util;

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUser("root@localhost"));
        Assert.assertEquals("root", util.getUser());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUser("root"));
        Assert.assertEquals("root", util.getUser());
    }

    private static Class<?> loadClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Test
    public void testGetPasswordMySQL_5_1() throws Exception {
        // with MySQL Connector 5.1.38, we use JDBC4Connection and its superclass ConnectionImpl
        // to get hold of the password.
        Class<?> connectionImpl = loadClass("com.mysql.jdbc.ConnectionImpl");
        Assume.assumeNotNull(connectionImpl);
        Field propsField = connectionImpl.getDeclaredField("props");
        Assert.assertNotNull("The field props is not existing", propsField);
    }

    @Test
    public void testGetPasswordMySQL_6() throws Exception {
        assumeJava8();
        // with MySQL Connector 6.0.4, the packages changed.
        Class<?> connectionImpl = loadClass("com.mysql.cj.jdbc.ConnectionImpl");
        Assume.assumeNotNull(connectionImpl);
        Field propsField2 = connectionImpl.getDeclaredField("props");
        Assert.assertNotNull("The field props is not existing", propsField2);
    }

    private static void assumeJava8() {
        String javaVersion = System.getProperty("java.version");
        int majorJava = Integer.parseInt(javaVersion.split("\\.")[1]);
        Assume.assumeTrue("Java Runtime too old - for this test at least java8 is required", majorJava >= 8);
    }

    @Test
    public void testTomcatJdbcConnection() throws Exception {
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(
                new JdbcConnection(MockedTomcatJdbcConnection.create("user", "xyz")));
        Assert.assertEquals("xyz", util.getPassword());
    }

    @Test
    public void testDatabasePropertiesFromFile() throws Exception {
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3306/testdb"));
        Assert.assertEquals("password-for-unit-testing", util.getPassword());
    }

    @Test
    public void testDatabasePasswordSystemProperty() throws Exception {
        System.setProperty(Configuration.LIQUIBASE_PASSWORD, "password-via-system-property");
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3306/testdb"));
        Assert.assertEquals("password-via-system-property", util.getPassword());
    }
}

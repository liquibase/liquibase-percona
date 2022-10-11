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
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ExtendWith;

import liquibase.database.jvm.JdbcConnection;

@ExtendWith(RestoreSystemPropertiesExtension.class)
public class DatabaseConnectionUtilTest {

    @Test
    public void testGetHost() {
        DatabaseConnectionUtil util;

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3306/testdb"));
        Assertions.assertEquals("localhost", util.getHost());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://localhost:3306/testdb"));
        Assertions.assertEquals("localhost", util.getHost());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://127.0.0.1:3306/testdb"));
        Assertions.assertEquals("127.0.0.1", util.getHost());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mariadb://127.0.0.1:3306/testdb"));
        Assertions.assertEquals("127.0.0.1", util.getHost());
    }

    @Test
    public void testGetPort() {
        DatabaseConnectionUtil util;

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3307/testdb"));
        Assertions.assertEquals("3307", util.getPort());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://localhost:3307/testdb"));
        Assertions.assertEquals("3307", util.getPort());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://localhost/testdb"));
        Assertions.assertEquals("3306", util.getPort());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mariadb://127.0.0.1:3307/testdb"));
        Assertions.assertEquals("3307", util.getPort());
}

    @Test
    public void testGetUser() {
        DatabaseConnectionUtil util;

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUser("root@localhost"));
        Assertions.assertEquals("root", util.getUser());

        util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUser("root"));
        Assertions.assertEquals("root", util.getUser());
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
        Assumptions.assumeFalse(connectionImpl == null, "The class com.mysql.jdbc.ConnectionImpl wasn't on the test classpath.");
        Field propsField = connectionImpl.getDeclaredField("props");
        Assertions.assertNotNull(propsField, "The field props is not existing");
    }

    @Test
    public void testGetPasswordMySQL_6() throws Exception {
        // with MySQL Connector 6.0.4, the packages changed.
        Class<?> connectionImpl = loadClass("com.mysql.cj.jdbc.ConnectionImpl");
        Assumptions.assumeFalse(connectionImpl == null, "The class com.mysql.cj.jdbc.ConnectionImpl wasn't on the test classpath.");
        Field propsField2 = connectionImpl.getDeclaredField("props");
        Assertions.assertNotNull(propsField2, "The field props is not existing");
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_11, disabledReason = "Tomcat JDBC 10.1.0 requires at least Java 11")
    public void testTomcatJdbcConnection() throws Exception {
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(
                new JdbcConnection(MockedTomcatJdbcConnection.create("user", "xyz")));
        Assertions.assertEquals("xyz", util.getPassword());
    }

    @Test
    public void testMariaDbJdbcConnectionPasswordInURL() throws Exception {
        String url = "jdbc:mariadb://127.0.0.1/db?user=user&password=xyz1";
        org.mariadb.jdbc.Configuration mariadbConfig = org.mariadb.jdbc.Configuration.parse(url);

        DatabaseConnectionUtil util = new DatabaseConnectionUtil(new JdbcConnection(
                MockedMariaDbConnection.create(mariadbConfig)));
        Assertions.assertEquals("xyz1", util.getPassword());
    }

    @Test
    public void testMariaDbJdbcConnectionPasswordInProps() throws Exception {
        String url = "jdbc:mariadb://127.0.0.1/db?user=user";
        Properties props = new Properties();
        props.setProperty("password", "xyz2");
        org.mariadb.jdbc.Configuration mariadbConfig = org.mariadb.jdbc.Configuration.parse(url, props);

        DatabaseConnectionUtil util = new DatabaseConnectionUtil(new JdbcConnection(
                MockedMariaDbConnection.create(mariadbConfig)));
        Assertions.assertEquals("xyz2", util.getPassword());
    }

    @Test
    public void testApacheCommonsDbcpPoolingConnection() throws Exception {
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(
                new JdbcConnection(MockedDbcpPoolingConnection.create("user", "xyz")));
        Assertions.assertEquals("xyz", util.getPassword());
    }

    @Test
    public void testApacheCommonsDbcp2PoolingConnection() throws Exception {
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(
                new JdbcConnection(MockedDbcp2PoolingConnection.create("user", "xyz")));
        Assertions.assertEquals("xyz", util.getPassword());
    }

    @Test
    public void testDatabasePropertiesFromFile() throws Exception {
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3306/testdb"));
        Assertions.assertEquals("password-for-unit-testing", util.getPassword());
    }

    @Test
    public void testDatabasePasswordSystemProperty() throws Exception {
        System.setProperty(Configuration.LIQUIBASE_PASSWORD, "password-via-system-property");
        DatabaseConnectionUtil util = new DatabaseConnectionUtil(MockDatabaseConnection.fromUrl("jdbc:mysql://user@localhost:3306/testdb"));
        Assertions.assertEquals("password-via-system-property", util.getPassword());
    }
}

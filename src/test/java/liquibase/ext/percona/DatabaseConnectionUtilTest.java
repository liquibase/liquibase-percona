package liquibase.ext.percona;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseConnectionUtilTest {
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

}

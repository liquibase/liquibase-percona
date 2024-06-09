package liquibase.ext.percona;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlterTableParserTest {

    @Test
    void simpleAlterTable() {
        AlterTableParser parser = new AlterTableParser("alter table xyz add column foo INT");
        assertValidParser(parser, null, "xyz", "add column foo INT");
    }

    @Test
    void simpleAlterTableLeadingSpace() {
        AlterTableParser parser = new AlterTableParser("   alter    table    xyz    add column foo INT   ");
        assertValidParser(parser, null, "xyz", "add column foo INT");
    }

    @Test
    void simpleAlterTableWithDatabase() {
        AlterTableParser parser = new AlterTableParser("alter table mydb.xyz add column foo INT");
        assertValidParser(parser, "mydb", "xyz", "add column foo INT");
    }

    @Test
    void simpleAlterTableWithDatabaseAndSpace() {
        AlterTableParser parser = new AlterTableParser("alter table mydb . xyz add column foo INT");
        assertValidParser(parser, "mydb", "xyz", "add column foo INT");
    }

    @Test
    void simpleAlterTableQuoted() {
        AlterTableParser parser = new AlterTableParser("alter table `xyz` add column `foo` INT");
        assertValidParser(parser, null, "xyz", "add column `foo` INT");
    }

    @Test
    void simpleAlterTableQuotedWithSpace() {
        AlterTableParser parser = new AlterTableParser("alter table `xyz abc` add column `foo` INT");
        assertValidParser(parser, null, "xyz abc", "add column `foo` INT");
    }

    @Test
    void simpleAlterTableQuotedWithDatabase() {
        AlterTableParser parser = new AlterTableParser("alter table `mydb`.`xyz` add column `foo` INT");
        assertValidParser(parser, "mydb", "xyz", "add column `foo` INT");
    }

    @Test
    void simpleAlterTableQuotedWithDatabaseAndSpace() {
        AlterTableParser parser = new AlterTableParser("alter table `mydb`.`xyz abc` add column `foo` INT");
        assertValidParser(parser, "mydb", "xyz abc", "add column `foo` INT");
    }

    @Test
    void simpleAlterTableAnsiQuoted() {
        AlterTableParser parser = new AlterTableParser("alter table \"xyz\" add column \"foo\" INT");
        assertValidParser(parser, null, "xyz", "add column \"foo\" INT");
    }

    @Test
    void simpleAlterTableAnsiQuotedWithSpace() {
        AlterTableParser parser = new AlterTableParser("alter table \"xyz abc\" add column \"foo\" INT");
        assertValidParser(parser, null, "xyz abc", "add column \"foo\" INT");
    }

    @Test
    void simpleAlterTableAnsiQuotedWithDatabase() {
        AlterTableParser parser = new AlterTableParser("alter table \"mydb\".\"xyz\" add column \"foo\" INT");
        assertValidParser(parser, "mydb", "xyz", "add column \"foo\" INT");
    }

    @Test
    void simpleAlterTableAnsiQuotedWithDatabaseAndSpace() {
        AlterTableParser parser = new AlterTableParser("alter table \"mydb\".\"xyz abc\" add column \"foo\" INT");
        assertValidParser(parser, "mydb", "xyz abc", "add column \"foo\" INT");
    }

    @Test
    void notAlterTable() {
        AlterTableParser parser = new AlterTableParser("create table xyz(foo INT)");
        assertFalse(parser.isValid());
        assertThrows(IllegalStateException.class, parser::getTargetDatabaseName);
        assertThrows(IllegalStateException.class, parser::getTargetTableName);
        assertThrows(IllegalStateException.class, parser::getAlterTableOptions);
    }

    private void assertValidParser(AlterTableParser parser, String expectedDatabaseName, String expectedTableName, String expectedAlterOptions) {
        assertTrue(parser.isValid());
        assertEquals(expectedDatabaseName, parser.getTargetDatabaseName());
        assertEquals(expectedTableName, parser.getTargetTableName());
        assertEquals(expectedAlterOptions, parser.getAlterTableOptions());
    }

}

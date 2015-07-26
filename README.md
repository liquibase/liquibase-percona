# Liquibase Percona Extension

Extension to support the tool `pt-online-schema-change` from Percona Toolkit.
This extension replaces the default *AddColumn* and *DropColumn* changes to use `pt-online-schema-change` instead
of SQL. This allows to perform a non-locking database upgrade.


## Supported Databases

MySQL is the only supported database.
The extension checks whether it is being run against a MySQL database. If not, it falls back to the default
AddColumn/DropColumn change of liquibase-core.


## Liquibase version(s) tested against

* Liquibase 3.2.0 (liquibase-percona 1.0.0)
* Liquibase 3.3.0 (liquibase-percona 1.1.1)


## Example

This changeset

    <changeSet id="2" author="Alice">
        <addColumn tableName="person">
            <column name="address" type="varchar(255)"/>
        </addColumn>
    </changeSet>

will execute the following command:

    pt-online-schema-change --alter="ADD COLUMN address VARCHAR(255)" --alter-foreign-keys-method=auto --host=127.0.0.1 --port=3306 --user=root --password=** --execute D=testdb,t=person


## Configuration

The extension supports the following java system properties:

* `liquibase.percona.failIfNoPT`: true/false. Default: false.
  If set to true, the database upate will fail, if the command `pt-online-schema-change` is not found.
  This can be used, to enforce, that percona toolkit is used.

* `liquibase.percona.noAlterSqlDryMode`: true/false. Default: false.
  When running *updateSQL* or *rollbackSQL* in order to generate a migration SQL file, the command line, that would
  be executed, will be added as a comment.
  In addition, the SQL statements (as produced by liquibase-core) will also be generated and output into the migration
  file. This allows to simply execute the generated migration SQL to perform an update. However, the Percona toolkit
  won't be used.
  If this property is set to `true`, then no such SQL statements will be output into the migration file.


## Changelog

### Version 1.1.1 (2015-07-26)

*   Fixed [#1](https://github.com/adangel/liquibase-percona/issues/1): Tables with foreign keys

### Version 1.1.0 (2014-11-06)

*   Initial version compatible with liquibase 3.3.0

### Version 1.0.0 (2014-10-09)

*   Initial version compatible with liquibase 3.2.0

## Using / Installing the extension

### Download

The jar files can be downloaded manually from maven:

<http://repo1.maven.org/maven2/com/github/adangel/liquibase/ext/liquibase-percona/>


### Command line liquibase

After extracting the zip file of liquibase, place `liquibase-percona-1.1.1.jar` file in the sub directory `lib`.
The shell script `liquibase` / `liquibase.bat` will automatically pick this up and the extension is available.

### Via maven

Add the following dependency to your project's pom file:

    <project>
        <dependencies>
            <dependency>
                <groupId>com.github.adangel.liquibase.ext</groupId>
                <artifactId>liquibase-percona</artifactId>
                <!-- use 1.0.0 or 1.1.1 -->
                <version>1.1.1</version>
                <scope>runtime</scope>
            </dependency>
        </dependencies>
    </project>

## Notes

The non-locking update is achieved using triggers. First a new temporary table is created, including the added or
dropped columns. Then the data is copied in chunks. While to copy is in progress, any newly created or deleted or
updated rows are copied, too. This is done by adding triggers to the original table. After the copy is finished, the
original table is dropped and the temporary table is renamed.

This means, that *pt-online-schema-change* cannot be used, if the table already uses triggers.

The command `pt-online-schema-change` is searched only on the `PATH`.


## Building this extension

Simply run `mvn clean install`.

In order to execute the integration tests, run `mvn clean install -Prun-its`.
Please note, that a MySQL server/Percona server is needed. See the properties *config_...* in `pom.xml` for
connection details.

## Common Problems

#### NoSuchMethodError: PerconaDropColumnChange.getColumns()Ljava/util/List

The full error message:

    Unexpected error running Liquibase: liquibase.exception.UnexpectedLiquibaseException:
    java.lang.NoSuchMethodError: liquibase.ext.percona.PerconaDropColumnChange.getColumns()Ljava/util/List;

This means, you are trying to use version 1.1.1 of the extension with liquibase 3.2.x. This is an unsupported
combination. For Liquibase 3.2.x, you'll need to use liquibase-percona 1.0.0


## References

* [Percona](http://www.percona.com/)
* [Percona Toolkit Documentation: pt-online-schema-change](http://www.percona.com/doc/percona-toolkit/2.2/pt-online-schema-change.html)
* [Liquibase Percona Extension](https://liquibase.jira.com/wiki/display/CONTRIB/Percona+Online+Schema+Change)
* [DZone: Avoiding MySQL ALTER Table Downtime](http://java.dzone.com/articles/avoiding-mysql-alter-table)

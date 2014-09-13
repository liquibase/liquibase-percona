# Liquibase Percona Extension

Extension to support the tool `pt-online-schema-change` from Percona Toolkit.
This extension replaces the default *AddColumn* and *DropColumn* changes to use `pt-online-schema-change` instead
of SQL. This allows to perform a non-locking database upgrade.

The extension checks whether it is being run against a MySQL database. If not, it falls back to the default
AddColumn/DropColumn change of liquibase-core.


## Example

This changeset

    <changeSet id="2" author="Alice">
        <addColumn tableName="person">
            <column name="address" type="varchar(255)"/>
        </addColumn>
    </changeSet>

will execute the following command:

    pt-online-schema-change --alter="ADD COLUMN address VARCHAR(255)" --host=127.0.0.1 --port=3306 --user=root --password=** --execute D=testdb,t=person


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

## References

* [Percona](http://www.percona.com/)
* [Percona Toolkit Documentation: pt-online-schema-change](http://www.percona.com/doc/percona-toolkit/2.2/pt-online-schema-change.html)
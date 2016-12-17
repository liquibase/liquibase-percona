# Liquibase Percona Extension

[![Build Status](https://travis-ci.org/adangel/liquibase-percona.svg?branch=master)](https://travis-ci.org/adangel/liquibase-percona)

Extension to support the tool `pt-online-schema-change` from Percona Toolkit.
This extension replaces a couple of the default changes to use `pt-online-schema-change` instead of SQL.
This allows to perform a non-locking database upgrade.


## Table Of Contents

*   [Supported Databases](#supported-databases)
*   [Liquibase version(s) tested against](#liquibase-versions-tested-against)
*   [Supported Changes and examples](#supported-changes-and-examples)
    *   [AddColumn](#addcolumn)
    *   [AddForeignKeyConstraint](#addforeignkeyconstraint)
    *   [AddUniqueConstraint](#adduniqueconstraint)
    *   [CreateIndex](#createindex)
    *   [DropColumn](#dropcolumn)
    *   [DropForeignKeyConstraint](#dropforeignkeyconstraint)
    *   [DropIndex](#dropindex)
    *   [DropUniqueConstraint](#dropuniqueconstraint)
    *   [ModifyDataType](#modifydatatype)
*   [Configuration](#configuration)
*   [Changelog](#changelog)
    *   [Version 1.3.0 (?????)](#version-130-)
    *   [Version 1.2.1 (2016-09-13)](#version-121-2016-09-13)
    *   [Version 1.2.0 (2016-04-02)](#version-120-2016-04-02)
    *   [Version 1.1.1 (2015-07-26)](#version-111-2015-07-26)
    *   [Version 1.1.0 (2014-11-06)](#version-110-2014-11-06)
    *   [Version 1.0.0 (2014-10-09)](#version-100-2014-10-09)
*   [Using / Installling the extension](#using--installing-the-extension)
    *   [Download](#download)
    *   [Command line liquibase](#command-line-liquibase)
    *   [Via Maven](#via-maven)
    *   [Using snapshots](#using-snapshots)
*   [Notes](#notes)
*   [Building this extension](#building-this-extension)
    *   [Integration testing](#integration-testing)
*   [Common Problems](#common-problems)
    *   [NoSuchMethodError: PerconaDropColumnChange.getColumns()Ljava/util/List](#nosuchmethoderror-perconadropcolumnchangegetcolumnsljavautillist)
*   [Sponsors](#sponsors)
*   [References](#references)



## Supported Databases

MySQL is the only supported database.
The extension checks whether it is being run against a MySQL database. If not, it falls back to the default
changes provided by liquibase-core.


## Liquibase version(s) tested against

* Liquibase 3.2.0 (liquibase-percona 1.0.0)
* Liquibase 3.3.0 (liquibase-percona 1.1.1)
* Liquibase 3.3.5 and 3.4.2 (liquibase-percona 1.2.1)
* Liquibase 3.3.5, 3.4.2, and 3.5.1 (liquibase-percona 1.2.2)


## Supported Changes and examples

The following changes are supported:

### AddColumn

Since: liquibase-percona 1.0.0

Automatic rollback supported? yes

Example:

    <changeSet id="2" author="Alice">
        <addColumn tableName="person">
            <column name="address" type="varchar(255)"/>
        </addColumn>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="ADD COLUMN address VARCHAR(255)" ...


### AddForeignKeyConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? yes

Example:

    <changeSet id="3" author="Alice">
        <addForeignKeyConstraint constraintName="fk_person_address"
            referencedTableName="person" referencedColumnNames="id"
            baseTableName="address" baseColumnNames="person_id"/>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="ADD CONSTRAINT fk_person_address FOREIGN KEY (person_id) REFERENCES person (id)" ...


### AddUniqueConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? yes

Example:

    <changeSet id="2" author="Alice">
        <addUniqueConstraint columnNames="id, name" tableName="person" constraintName="uq_id_name"/>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="ADD CONSTRAINT uq_id_name UNIQUE (id, name)" ...


### CreateIndex

Since: liquibase-percona 1.2.0

Automatic rollback supported? yes

Example:

    <changeSet id="2" author="Alice">
        <createIndex indexName="emailIdx" tableName="person" unique="true">
            <column name="email"/>
        </createIndex>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="ADD UNIQUE INDEX emailIdx (email)" ...


### DropColumn

Since: liquibase-percona 1.0.0

Automatic rollback supported? no

Example:

    <changeSet id="2" author="Alice">
        <dropColumn tableName="person" columnName="age"/>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="DROP COLUMN age" ...


### DropForeignKeyConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? no

Example:

    <changeSet id="4" author="Alice">
        <dropForeignKeyConstraint baseTableName="address" constraintName="fk_person_address" />
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="DROP FOREIGN KEY _fk_person_address" ...


### DropUniqueConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? no

Example:

    <changeSet id="3" author="Alice">
        <dropUniqueConstraint tableName="person" constraintName="uq_id_name"/>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="DROP KEY uq_id_name" ...


### DropIndex

Since: liquibase-percona 1.2.0

Automatic rollback supported? no

Example:

    <changeSet id="3" author="Alice">
        <dropIndex indexName="emailIdx" tableName="person"/>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="DROP INDEX emailIdx" ...


### ModifyDataType

Since: liquibase-percona 1.2.0

Automatic rollback supported? no

Example:

    <changeSet id="2" author="Alice">
        <modifyDataType tableName="person" columnName="email" newDataType="VARCHAR(400)"/>
    </changeSet>

Corresponding command:

    pt-online-schema-change --alter="MODIFY email VARCHAR(400)" ...



## Configuration

The extension supports the following java system properties:

*   `liquibase.percona.failIfNoPT`: true/false. **Default: false**.
    If set to true, the database update will fail, if the command `pt-online-schema-change` is not found.
    This can be used, to enforce, that percona toolkit is used.

*   `liquibase.percona.noAlterSqlDryMode`: true/false. **Default: false**.
    When running *updateSQL* or *rollbackSQL* in order to generate a migration SQL file, the command line, that would
    be executed, will be added as a comment.
    In addition, the SQL statements (as produced by liquibase-core) will also be generated and output into the migration
    file. This allows to simply execute the generated migration SQL to perform an update. However, the Percona toolkit
    won't be used.
    If this property is set to `true`, then no such SQL statements will be output into the migration file.

*   `liquibase.percona.skipChanges`: comma separated list of changes. **Default: <empty>**.
    This option can be used in order to selectively disable one or more changes. If a change is disabled, then
    the change will be executed by the default liquibase core implementation and *percona toolkit won't be used*.
    By default, this property is empty, so that all supported changes are executed using the percona toolkit.
    Example: Set this to `addColumn,dropColumn` in order to not use percona for adding/dropping a column.
    
*   `liquibase.percona.options`: String of options. **Default: <empty>**.
    This option allows the user to pass additional command line options to pt-online-schema-change. This e.g. can
    be used in complication replication setup to change the way slaves are detected and how their state is used.
    You can also specify a percona configuration file via `--config file.conf`,
    see [Configuration Files](https://www.percona.com/doc/percona-toolkit/2.2/configuration_files.html).
    Multiple options are separated by space. If argument itself contains a space, it must be quoted with
    double-quotes, e.g. `--config "filename with spaces.conf`.



You can set these properties by using the standard java `-D` option:

    java -Dliquibase.percona.skipChanges=createIndex,dropColumn -jar liquibase.jar ...

Note: You'll have to call liquibase via "java -jar" as otherwise the system property cannot be set. You'll also
need to make sure, that the liquibase-percona.jar file is on the classpath via the "--classpath" option.

When executing liquibase through maven, you can use the [Properties Maven Plugin](http://www.mojohaus.org/properties-maven-plugin/usage.html#set-system-properties) to set the system property. An example can be found in the "createIndexSkipped"
integration test.


## Changelog

### Version 1.3.0 (?????)

*   Upgraded liquibase to 3.5.1
*   Support for MySQL Connector 6.0.x in addition to 5.1.x.
*   Fixed [#7](https://github.com/adangel/liquibase-percona/issues/7): Foreign key constraints of AddColumn is ignored
*   Fixed [#8](https://github.com/adangel/liquibase-percona/issues/8): Support addForeignKeyConstraintChange, addUniqueConstraintChange
*   Fixed [#10](https://github.com/adangel/liquibase-percona/issues/10): Build fails with java7: UnsupportedClassVersion when running DatabaseConnectionUtilTest.testGetPasswordMySQL\_6\_0\_4

### Version 1.2.1 (2016-09-13)

*   [PR #4](https://github.com/adangel/liquibase-percona/pull/4): Allow passing additional command line options to pt-online-schema-change
*   [PR #5](https://github.com/adangel/liquibase-percona/pull/5): Support afterColum attribute

### Version 1.2.0 (2016-04-02)

*   Fixed [#2](https://github.com/adangel/liquibase-percona/issues/2): Adding indexes via pt-online-schema-change
*   Fixed [#3](https://github.com/adangel/liquibase-percona/issues/3): Altering column data types via pt-online-schema-change
*   Added configuration property "liquibase.percona.skipChanges"
*   Upgraded liquibase to 3.4.2

### Version 1.1.1 (2015-07-26)

*   Fixed [#1](https://github.com/adangel/liquibase-percona/issues/1): Tables with foreign keys

### Version 1.1.0 (2014-11-06)

*   Initial version compatible with liquibase 3.3.0

### Version 1.0.0 (2014-10-09)

*   Initial version compatible with liquibase 3.2.0

## Using / Installing the extension

### Download

The jar files can be downloaded manually from maven:

<http://repo.maven.apache.org/maven2/com/github/adangel/liquibase/ext/liquibase-percona/>


### Command line liquibase

After extracting the zip file of liquibase, place `liquibase-percona-1.2.1.jar` file in the sub directory `lib`.
The shell script `liquibase` / `liquibase.bat` will automatically pick this up and the extension is available.

### Via Maven

Add the following dependency to the liquibase plugin:

    <dependency>
        <groupId>com.github.adangel.liquibase.ext</groupId>
        <artifactId>liquibase-percona</artifactId>
        <version>1.2.1</version>
    </dependency>


### Using snapshots

Snapshot builds contain the latest features which are not yet available in a release.

Download: <https://oss.sonatype.org/content/repositories/snapshots/com/github/adangel/liquibase/ext/liquibase-percona/>

Enable the snapshot repository via Maven:

    <project>
        <repositories>
            <repository>
                <id>sonatype-nexus-snapshots</id>
                <name>Sonatype Nexus Snapshots</name>
                <url>https://oss.sonatype.org/content/repositories/snapshots</url>
                <releases>
                    <enabled>false</enabled>
                </releases>
                <snapshots>
                    <enabled>true</enabled>
                </snapshots>
            </repository>
        </repositories>
    </project>

And just use the latest SNAPSHOT version for liquibase-percona dependency, e.g. `1.2.2-SNAPSHOT`:

    <dependency>
        <groupId>com.github.adangel.liquibase.ext</groupId>
        <artifactId>liquibase-percona</artifactId>
        <version>1.2.2-SNAPSHOT</version>
    </dependency>



## Notes

The non-locking update is achieved using triggers. First a new temporary table is created, including the added or
dropped columns. Then the data is copied in chunks. While the copy is in progress, any newly created or deleted or
updated rows are copied, too. This is done by adding triggers to the original table. After the copy is finished, the
original table is dropped and the temporary table is renamed.

This means, that *pt-online-schema-change* **cannot be used**, if the table already uses triggers.

The command `pt-online-schema-change` is searched only on the `PATH`. Depending on the property
`liquibase.percona.failIfNoPT` the update will fail or will just run without using pt-online-schema-change and
potentially lock the table for the duration of the update.


## Building this extension

Simply run `mvn clean verify`.
You'll find the jar-file in the `target/` subdirectory.


### Integration testing

In order to execute the integration tests, run `mvn clean verify -Prun-its`.

Please note, that you'll need:

1.  [docker](https://www.docker.com/).
    During the pre-integration-test phase the [official mysql image](https://hub.docker.com/_/mysql/) will be started.
    Under debian, execute `sudo apt-get install docker.io`.
2.  [percona toolkit](https://www.percona.com/downloads/percona-toolkit/).
    The command line tools need to be available on your `PATH`.
    The toolkit requires perl with mysql dbi libraries. Under debian, execute `sudo apt-get install libdbd-mysql-perl`.

See the properties *config_...* in `pom.xml` for connection details for the mysql docker instance.

To run a single integration test, execute maven like this: `mvn verify -Prun-its -Dinvoker.test=addColumn*,dropColumn`

## Common Problems

#### NoSuchMethodError: PerconaDropColumnChange.getColumns()Ljava/util/List

The full error message:

    Unexpected error running Liquibase: liquibase.exception.UnexpectedLiquibaseException:
    java.lang.NoSuchMethodError: liquibase.ext.percona.PerconaDropColumnChange.getColumns()Ljava/util/List;

This means, you are trying to use version 1.1.1 of the extension with liquibase 3.2.x. This is an unsupported
combination. For Liquibase 3.2.x, you'll need to use liquibase-percona 1.0.0

## Sponsors

Some development has been sponsored by [billforward.net](http://www.billforward.net/),
a highly flexible & unified billing platform.



## References

* [Percona](http://www.percona.com/)
* [Percona Toolkit Documentation: pt-online-schema-change](http://www.percona.com/doc/percona-toolkit/2.2/pt-online-schema-change.html)
* [Liquibase Percona Extension](https://liquibase.jira.com/wiki/display/CONTRIB/Percona+Online+Schema+Change)
* [DZone: Avoiding MySQL ALTER Table Downtime](http://java.dzone.com/articles/avoiding-mysql-alter-table)

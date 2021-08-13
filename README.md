# Liquibase Percona Extension

[![Build Status](https://github.com/liquibase/liquibase-percona/workflows/Build/badge.svg)](https://github.com/liquibase/liquibase-percona/actions)
[![Reproducible Builds](https://img.shields.io/badge/Reproducible_Builds-ok-green?labelColor=blue)](https://github.com/jvm-repo-rebuild/reproducible-central#org.liquibase.ext:liquibase-percona)
[![Maven Central](https://img.shields.io/maven-central/v/org.liquibase.ext/liquibase-percona)](https://search.maven.org/artifact/org.liquibase.ext/liquibase-percona)

Extension to support the tool `pt-online-schema-change` from [Percona Toolkit](https://www.percona.com/doc/percona-toolkit/LATEST/index.html).
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
    *   [UsePercona flag](#usepercona-flag)
    *   [PerconaOptions flag](#perconaoptions-flag)
    *   [System Properties](#system-properties)
*   [Changelog](#changelog)
    *   [Version 4.4.3 (2021-08-12)](#version-443-2021-08-12)
    *   [Version 4.4.2 (2021-07-23)](#version-442-2021-07-23)
    *   [Version 4.4.1 (2021-07-18)](#version-441-2021-07-18)
    *   [Version 4.4.0 (2021-06-20)](#version-440-2021-06-20)
    *   [Version 4.3.5 (2021-05-24)](#version-435-2021-05-24)
    *   [Version 4.3.4 (2021-04-28)](#version-434-2021-04-28)
    *   [Version 4.3.3 (2021-04-13)](#version-433-2021-04-13)
    *   [Version 4.3.2 (2021-03-26)](#version-432-2021-03-26)
    *   [Version 4.3.1 (2021-02-23)](#version-431-2021-02-23)
    *   [Version 2.0.0 (2021-02-04)](#version-200-2021-02-04)
    *   [Version 1.7.1 (2021-01-28)](#version-171-2021-01-28)
    *   [Version 1.7.0 (2020-07-04)](#version-170-2020-07-04)
    *   [Version 1.6.0 (2019-04-20)](#version-160-2019-04-20)
    *   [Version 1.5.2 (2019-04-14)](#version-152-2019-04-14)
    *   [Version 1.5.1 (2018-11-10)](#version-151-2018-11-10)
    *   [Version 1.5.0 (2018-09-30)](#version-150-2018-09-30)
    *   [Version 1.4.1 (2018-09-27)](#version-141-2018-09-27)
    *   [Version 1.4.0 (2017-07-21)](#version-140-2017-07-21)
    *   [Version 1.3.1 (2017-07-21)](#version-131-2017-07-21)
    *   [Version 1.3.0 (2016-12-18)](#version-130-2016-12-18)
    *   [Version 1.2.1 (2016-09-13)](#version-121-2016-09-13)
    *   [Version 1.2.0 (2016-04-02)](#version-120-2016-04-02)
    *   [Version 1.1.1 (2015-07-26)](#version-111-2015-07-26)
    *   [Version 1.1.0 (2014-11-06)](#version-110-2014-11-06)
    *   [Version 1.0.0 (2014-10-09)](#version-100-2014-10-09)
*   [Using / Installing the extension](#using--installing-the-extension)
    *   [Download](#download)
    *   [Command line liquibase](#command-line-liquibase)
    *   [Via Maven](#via-maven)
    *   [Docker](#docker)
    *   [Using snapshots](#using-snapshots)
*   [Notes](#notes)
*   [Building this extension](#building-this-extension)
    *   [Integration testing](#integration-testing)
*   [Common Problems](#common-problems)
    *   [NoSuchMethodError: PerconaDropColumnChange.getColumns()Ljava/util/List](#nosuchmethoderror-perconadropcolumnchangegetcolumnsljavautillist)
*   [Sponsors](#sponsors)
*   [References](#references)



## Supported Databases

MySQL and MariaDB (since 4.3.2) are the only supported databases.
The extension checks whether it is being run against a MySQL/MariaDB database. If not, it falls back to the default
changes provided by liquibase-core.


## Liquibase version(s) tested against

* Liquibase 3.2.0 (liquibase-percona 1.0.0)
* Liquibase 3.3.0 (liquibase-percona 1.1.1)
* Liquibase 3.3.5 and 3.4.2 (liquibase-percona 1.2.1)
* Liquibase 3.3.5, 3.4.2, and 3.5.1 (liquibase-percona 1.2.2)
* Liquibase 3.3.5, 3.4.2, and 3.5.3 (liquibase-percona 1.3.1, 1.4.0)
* Liquibase 3.3.5, 3.4.2, and 3.5.4 (liquibase-percona 1.4.1)
* Liquibase 3.3.5, 3.4.2, 3.5.5, and 3.6.2 (liquibase-percona 1.5.2). Percona Toolkit 3.0.12.
* Liquibase 3.3.5, 3.4.2, 3.5.5, and 3.6.3 (liquibase-percona 1.6.0). Percona Toolkit 3.0.13.
* Liquibase 3.5.5, 3.6.3, 3.7.0, 3.8.9, 3.9.0, and 3.10.1 (liquibase-percona 1.7.0). Percona Toolkit 3.2.0.
* Liquibase 3.5.5, 3.6.3, 3.7.0, 3.8.9, 3.9.0, and 3.10.3 (liquibase-percona 1.7.1). Percona Toolkit 3.3.0.
* Liquibase 4.0.0, 4.1.1, 4.2.2 (liquibase-percona 2.0.0). Percona Toolkit 3.3.0.
* Liquibase 4.0.0, 4.1.1, 4.2.2, 4.3.5, 4.4.2 (liquibase-percona 4.4.2). Percona Toolkit 3.3.1.

## Supported Changes and examples

The following changes are supported:

### AddColumn

Since: liquibase-percona 1.0.0

Automatic rollback supported? yes

Example:

```xml
<changeSet id="2" author="Alice">
    <addColumn tableName="person">
        <column name="address" type="varchar(255)"/>
    </addColumn>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="ADD COLUMN address VARCHAR(255)" ...


### AddForeignKeyConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? yes

Example:

```xml
<changeSet id="3" author="Alice">
    <addForeignKeyConstraint constraintName="fk_person_address"
        referencedTableName="person" referencedColumnNames="id"
        baseTableName="address" baseColumnNames="person_id"/>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="ADD CONSTRAINT fk_person_address FOREIGN KEY (person_id) REFERENCES person (id)" ...


### AddPrimaryKey

Since: liquibase-percona 1.7.0

Automatic rollback supported? no

Example:

```xml
<changeSet id="2" author="Alice">
    <addPrimaryKey tableName="person" columnNames="id, name"/>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="DROP PRIMARY KEY, ADD PRIMARY KEY (id, name)" ...

**Note:** When the table has already a primary key, a "DROP PRIMARY KEY" statement is added to the
alter command first. By default, the pt-online-schema-change will not execute this change,
you have to set the additional option `--no-check-alter` first
(see [check-alter](https://www.percona.com/doc/percona-toolkit/LATEST/pt-online-schema-change.html#id1)).
Make sure to read this section completely.

In order to figure out, whether a primary key exists already (and therefore the DROP PRIMARY KEY statement is needed),
a database connection is required. This means, the generated migration SQL will be wrong (it only contains the
ADD PRIMARY KEY statement).

Automatic rollback is not supported by this percona change (as opposed to the plain liquibase addPrimaryKey change).
pt-osc usually needs a primary key or a unique key in order to operate properly. If the table has no such keys,
it most likely will refuse to operate.

### AddUniqueConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? yes

Example:

```xml
<changeSet id="2" author="Alice">
    <addUniqueConstraint columnNames="id, name" tableName="person" constraintName="uq_id_name"/>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="ADD CONSTRAINT uq_id_name UNIQUE (id, name)" ...

**Note:** pt-online-schema-change is executed with the option `--nocheck-unique-key-change`. This enables to
add a unique index, but can cause data loss, since duplicated rows are ignored.
See [Percona Toolkit Documentation](https://www.percona.com/doc/percona-toolkit/LATEST/pt-online-schema-change.html#id7)
for more information.


### CreateIndex

Since: liquibase-percona 1.2.0

Automatic rollback supported? yes

Example:

```xml
<changeSet id="2" author="Alice">
    <createIndex indexName="emailIdx" tableName="person" unique="true">
        <column name="email"/>
    </createIndex>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="ADD UNIQUE INDEX emailIdx (email)" ...

**Note:** pt-online-schema-change is executed with the option `--nocheck-unique-key-change`. This enables to
add a unique index, but can cause data loss, since duplicated rows are ignored.
See [Percona Toolkit Documentation](https://www.percona.com/doc/percona-toolkit/LATEST/pt-online-schema-change.html#id7)
for more information.


### DropColumn

Since: liquibase-percona 1.0.0

Automatic rollback supported? no

Example:

```xml
<changeSet id="2" author="Alice">
    <dropColumn tableName="person" columnName="age"/>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="DROP COLUMN age" ...


### DropForeignKeyConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? no

Example:

```xml
<changeSet id="4" author="Alice">
    <dropForeignKeyConstraint baseTableName="address" constraintName="fk_person_address" />
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="DROP FOREIGN KEY _fk_person_address" ...


### DropUniqueConstraint

Since: liquibase-percona 1.3.0

Automatic rollback supported? no

Example:

```xml
<changeSet id="3" author="Alice">
    <dropUniqueConstraint tableName="person" constraintName="uq_id_name"/>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="DROP KEY uq_id_name" ...


### DropIndex

Since: liquibase-percona 1.2.0

Automatic rollback supported? no

Example:

```xml
<changeSet id="3" author="Alice">
    <dropIndex indexName="emailIdx" tableName="person"/>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="DROP INDEX emailIdx" ...


### ModifyDataType

Since: liquibase-percona 1.2.0

Automatic rollback supported? no

Example:

```xml
<changeSet id="2" author="Alice">
    <modifyDataType tableName="person" columnName="email" newDataType="VARCHAR(400)"/>
</changeSet>
```

Corresponding command:

    pt-online-schema-change --alter="MODIFY email VARCHAR(400)" ...



## Configuration

### UsePercona flag

Each change allows to enable or disable the usage of percona toolkit via the property `usePercona`.
By default, the percona toolkit is used, see also the system property `liquibase.percona.defaultOn`.

Example:

```yaml
- changeSet:
    id: 2
    author: Alice
    changes:
      - addColumn:
          tableName: person
          usePercona: false
          columns:
            - column:
                name: address
                type: varchar(255)
```

This flag exists since liquibase-percona 1.3.0

It is supported by using the YAML format and since liquibase 3.6.0, you can use it in XML changesets, too:

```xml
<addColumn tableName="person"
        xmlns:liquibasePercona="http://www.liquibase.org/xml/ns/dbchangelog-ext/liquibase-percona"
        liquibasePercona:usePercona="false">
    <column name="address" type="varchar(255)"/>
</addColumn>
```

### PerconaOptions flag

Each change allows to specify options that are used when executing pt-osc. If specified, this option
overrides the system property [`liquibase.percona.options`](#system-properties). If not specified, then
the system property will be used.

Example:

```yaml
- changeSet:
    id: 2
    author: Alice
    changes:
      - addColumn:
          tableName: person
          perconaOptions: "--alter-foreign-keys-method=auto"
          columns:
            - column:
                name: address
                type: varchar(255)
```

This flag exists since liquibase-percona 2.0.0.

It is supported by using the YAML format and in XML changesets:

```xml
<addColumn tableName="person"
        xmlns:liquibasePercona="http://www.liquibase.org/xml/ns/dbchangelog-ext/liquibase-percona"
        liquibasePercona:perconaOptions="--alter-foreign-keys-method=auto">
    <column name="address" type="varchar(255)"/>
</addColumn>
```

### System Properties

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

*   `liquibase.percona.skipChanges`: comma separated list of changes. **Default: &lt;empty&gt;**.
    This option can be used in order to selectively disable one or more changes. If a change is disabled, then
    the change will be executed by the default liquibase core implementation and *percona toolkit won't be used*.
    By default, this property is empty, so that all supported changes are executed using the percona toolkit.
    Example: Set this to `addColumn,dropColumn` in order to not use percona for adding/dropping a column.

*   `liquibase.percona.options`: String of options. **Default: --alter-foreign-keys-method=auto --nocheck-unique-key-change**.
    Since liquibase-percona 1.2.1. Default value changed with liquibase-percona 1.6.0.
    This option allows the user to pass additional command line options to pt-online-schema-change. This e.g. can
    be used in complicated replication setup to change the way slaves are detected and how their state is used.
    You can also specify a percona configuration file via `--config file.conf`,
    see [Configuration Files](https://www.percona.com/doc/percona-toolkit/2.2/configuration_files.html).
    Multiple options are separated by space. If argument itself contains a space, it must be quoted with
    double-quotes, e.g. `--config "filename with spaces.conf"`.

*   `liquibase.percona.defaultOn`: true/false. **Default: true**. Since liquibase-percona 1.3.0
    This options allows to change the default behavior for the [UsePercona flag](#usepercona-flag). By default,
    all changes, that do not explicitly specify this flag, use the value from this system property.
    Setting this property to `false` allows to control for each single change, whether to use Percona Toolkit
    or not.

*   `liquibase.password`: String with the password needed to connect to the database. **Default: &lt;empty&gt;**.
    Since liquibase-percona 1.4.0.
    With this property, you can shortcut the automatic detection of the password from the underlying
    `java.sql.Connection` (if that fails) or from the default `liquibase.properties` file. If this property is set,
    then it is used for the password when executing `pt-online-schema-change`.

*   `liquibase.percona.path`: Path to the percona toolkit directory, where the tool
    `pt-online-schema-change` is installed. **Default: &lt;empty&gt;**.
    Since liquibase-percona 1.4.1.
    With this property, you can select a specific toolkit installation. If this property is not set, then the
    toolkit will be searched on the `PATH`. You need to specify the `bin` subfolder of the Percona Toolkit
    distribution.

*   `liquibase.percona.ptdebug`: true/false. **Default: false**. Since liquibase-percona 1.5.0
    This option enables the debug output of pt-osc by setting the environment variable `PTDEBUG` before
    starting pt-osc.

*   `liquibase.percona.keepAlive`: true/false. **Default: true** Since liquibase-percona 4.4.0
    This option allows to disable the keepalive thread if there are any problems with it. The keepalive thread
    pings the database while pt-online-schema-change is executing. This avoids that the server closes
    liquibase's connection as it is idle during pt-osc. The server variable "wait_timeout" controls
    when the connection is considered stale and dropped by the server. This variable is used to
    determine how often the server will be pinged.

You can set these properties by using the standard java `-D` option:

    java -Dliquibase.percona.skipChanges=createIndex,dropColumn -jar liquibase.jar ...

Note: You'll have to call liquibase via "java -jar" as otherwise the system property cannot be set. You'll also
need to make sure, that the liquibase-percona.jar file is on the classpath via the "--classpath" option.

When executing liquibase through maven, you can use the [Properties Maven Plugin](http://www.mojohaus.org/properties-maven-plugin/usage.html#set-system-properties) to set the system property. An example can be found in the "createIndexSkipped"
integration test.


## Changelog

### Version 4.4.3 (2021-08-12)

*   Support for Liquibase 4.4.3.

### Version 4.4.2 (2021-07-23)

*   Support for Liquibase 4.4.2.

### Version 4.4.1 (2021-07-18)

*   Support for Liquibase 4.4.1.

*   [#122](https://github.com/liquibase/liquibase-percona/pull/122): Add docker image with liquibase, liquibase-percona and percona toolkit

### Version 4.4.0 (2021-06-20)

*  Support for Liquibase 4.4.0.

*   [PR #112](https://github.com/liquibase/liquibase-percona/pull/112): Fixing typos - [Jasper Vandemalle](https://github.com/jasper-vandemalle)
*   [#106](https://github.com/liquibase/liquibase-percona/issues/106): MySQL connection times out after pt-online-schema-change run
*   [#118](https://github.com/liquibase/liquibase-percona/pull/118): Use catalogName instead of schemaName

### Version 4.3.5 (2021-05-24)

*  Support for Liquibase 4.3.5.

### Version 4.3.4 (2021-04-28)

*  Support for Liquibase 4.3.4.

### Version 4.3.3 (2021-04-13)

*  Support for Liquibase 4.3.3.

### Version 4.3.2 (2021-03-26)

*   [#60](https://github.com/liquibase/liquibase-percona/issues/60): Add support for MariaDB JConnector
*   [#85](https://github.com/liquibase/liquibase-percona/issues/85): liquibase-percona 4.3.1 is not reproducible anymore
*   [#88](https://github.com/liquibase/liquibase-percona/pull/88): Support for Liquibase 4.3.2

### Version 4.3.1 (2021-02-23)

*   The maven coordinates have changed. This extension is now available like the other liquibase extensions in
    the group `org.liquibase.ext`.
    
    In order to add this extension, use the following snippet:
    
        <dependency>
            <groupId>org.liquibase.ext</groupId>
            <artifactId>liquibase-percona</artifactId>
            <version>4.3.1</version>
        </dependency>
    

*   [#66](https://github.com/liquibase/liquibase-percona/issues/66): Change maven coordinates to be org.liquibase.ext
*   [#74](https://github.com/liquibase/liquibase-percona/pull/74): Update Liquibase to 4.3.0
*   Support for Liquibase 4.3.1
*   Alignment with existing release process

### Version 2.0.0 (2021-02-04)

*   The minimum Java runtime version is now Java 1.8.
*   Liquibase 4+ is supported.
*   Support for older liquibase versions has been dropped.
*   The XML namespace for this extension is now "http://www.liquibase.org/xml/ns/dbchangelog-ext/liquibase-percona".
    
    This only is affecting you, if you use the [UsePercona flag](#usepercona-flag).
    
    There is now also a XSD schema available, if you want to validate your XML changeset:
    <https://raw.githubusercontent.com/liquibase/liquibase-percona/liquibase-percona-2.0.0/src/main/resources/dbchangelog-ext-liquibase-percona.xsd>
    
    See the file [test-changelog.xml](https://github.com/liquibase/liquibase-percona/blob/master/src/test/resources/liquibase/ext/percona/changelog/test-changelog.xml) for an example.
    
    Note: Usage of the schema is optional. In order to use the custom flags provided by this extension, you
    only need to declare the namespace.
*   The checksum calculation for changes that used [UsePercona flag](#usepercona-flag) changed. You might
    need to recreate the changelog entries for these changes with the
    [clearCheckSums](https://docs.liquibase.com/commands/community/clearchecksums.html) command.
    See [#64](https://github.com/liquibase/liquibase-percona/issues/64) for the explanation.

*   Fixed [#56](https://github.com/liquibase/liquibase-percona/issues/56): Support liquibase 4.x
*   Fixed [#57](https://github.com/liquibase/liquibase-percona/issues/57): Support perconaOptions per change
*   Fixed [#64](https://github.com/liquibase/liquibase-percona/issues/64): Different changeset checksums with and without liquibase-percona
*   Fixed [#65](https://github.com/liquibase/liquibase-percona/issues/65): Make build reproducible

### Version 1.7.1 (2021-01-28)

*   Fixed [#58](https://github.com/liquibase/liquibase-percona/pull/58): Update versions (liquibase, percona-toolkit, mysql)

### Version 1.7.0 (2020-07-04)

*   Fixed [#35](https://github.com/liquibase/liquibase-percona/issues/35): Add support for AddPrimaryKeyChange
*   Fixed [#37](https://github.com/liquibase/liquibase-percona/issues/37): Using quotes for liquibase.percona.options doesn't always work
*   Fixed [#53](https://github.com/liquibase/liquibase-percona/issues/53): Update to support latest liquibase 3.10.1
*   Fixed [#54](https://github.com/liquibase/liquibase-percona/issues/54): Update mysql-connector-java to 8.0.20
*   Fixed [#55](https://github.com/liquibase/liquibase-percona/issues/55): Update percona toolkit to 3.2.0

### Version 1.6.0 (2019-04-20)

The minimum Java runtime version is now Java 1.7.

The system property `liquibase.percona.options` uses now a default value of `--alter-foreign-keys-method=auto --nocheck-unique-key-change`.
These two options are **not** added by default anymore when pt-osc is executed. They are added
now via the additional options system property. In case you have overridden this system property, make sure, to add
these options as well, if you need them.

*   Fixed [#29](https://github.com/liquibase/liquibase-percona/issues/29): Allow to override --nocheck-unique-key-changes and --alter-foreign-keys-method=auto
*   Fixed [#30](https://github.com/liquibase/liquibase-percona/issues/30): Update liquibase

### Version 1.5.2 (2019-04-14)

*   Fixed [#28](https://github.com/liquibase/liquibase-percona/issues/28): Strange behavior when liquibase.percona.defaultOn is false

### Version 1.5.1 (2018-11-10)

*   Fixed [#26](https://github.com/liquibase/liquibase-percona/issues/26): Stack Overflow using defaultOn=false System Property
*   [PR #27](https://github.com/liquibase/liquibase-percona/pull/27): fix a typo - [kennethinsnow](https://github.com/kennethinsnow)

### Version 1.5.0 (2018-09-30)

`pt-online-schema-change` is executed now with the option `--nocheck-unique-key-change`.
This enables to add unique indexes, but can cause data loss, since duplicated rows are ignored.
See [Percona Toolkit Documentation](https://www.percona.com/doc/percona-toolkit/LATEST/pt-online-schema-change.html#id7)
for more information.

The plugin is only compatible with version 3.0.12 or later of Percona Toolkit.

*   Upgraded liquibase to 3.5.5
*   Verified compatibility to liquibase 3.6.2
*   Fixed [#14](https://github.com/liquibase/liquibase-percona/issues/14): Rollback of foreign key constraint changing constraint names problem
*   Fixed [#15](https://github.com/liquibase/liquibase-percona/issues/15): Unique key constraint cannot be added
*   Fixed [#20](https://github.com/liquibase/liquibase-percona/issues/20): Support "UsePercona flag" in XML changelogs
*   Fixed [#22](https://github.com/liquibase/liquibase-percona/issues/22): Cross database bug

### Version 1.4.1 (2018-09-27)

*   Fixed [#16](https://github.com/liquibase/liquibase-percona/issues/16): Failing test PerconaAddForeignKeyConstraintChangeTest
*   Fixed [#17](https://github.com/liquibase/liquibase-percona/issues/17): Include Percona Toolkit into integration test
*   Fixed [#18](https://github.com/liquibase/liquibase-percona/issues/18): Use spotbugs instead of findbugs
*   Fixed [#19](https://github.com/liquibase/liquibase-percona/issues/19): Upgrade liquibase to 3.5.4
*   Fixed [#21](https://github.com/liquibase/liquibase-percona/issues/21): Couldn't determine password: JdbcConnection is unsupported: dbcp.PoolingDataSource$PoolGuardConnectionWrapper
*   Fixed [#23](https://github.com/liquibase/liquibase-percona/pull/23): Add support for dbcp2
*   Added new system property `liquibase.percona.path` to specify the path where Percona Toolkit is installed.

### Version 1.4.0 (2017-07-21)

*   Fixed [#13](https://github.com/liquibase/liquibase-percona/issues/13): Use default liquibase.properties as fallback

### Version 1.3.1 (2017-07-21)

*   Fixed [#12](https://github.com/liquibase/liquibase-percona/issues/12): Cannot run migrations with the percona extension on a Spring Boot app with embedded Tomcat

### Version 1.3.0 (2016-12-18)

*   Upgraded liquibase to 3.5.3
*   Support for MySQL Connector 6.0.x in addition to 5.1.x.
*   Fixed [#7](https://github.com/liquibase/liquibase-percona/issues/7): Foreign key constraints of AddColumn is ignored
*   Fixed [#8](https://github.com/liquibase/liquibase-percona/issues/8): Support addForeignKeyConstraintChange, addUniqueConstraintChange
*   Fixed [#9](https://github.com/liquibase/liquibase-percona/issues/9): Support for enabling pt-online-schema-changes on a per-change basis
*   Fixed [#10](https://github.com/liquibase/liquibase-percona/issues/10): Build fails with java7: UnsupportedClassVersion when running DatabaseConnectionUtilTest.testGetPasswordMySQL\_6

### Version 1.2.1 (2016-09-13)

*   [PR #4](https://github.com/liquibase/liquibase-percona/pull/4): Allow passing additional command line options to pt-online-schema-change
*   [PR #5](https://github.com/liquibase/liquibase-percona/pull/5): Support afterColumn attribute

### Version 1.2.0 (2016-04-02)

*   Fixed [#2](https://github.com/liquibase/liquibase-percona/issues/2): Adding indexes via pt-online-schema-change
*   Fixed [#3](https://github.com/liquibase/liquibase-percona/issues/3): Altering column data types via pt-online-schema-change
*   Added configuration property "liquibase.percona.skipChanges"
*   Upgraded liquibase to 3.4.2

### Version 1.1.1 (2015-07-26)

*   Fixed [#1](https://github.com/liquibase/liquibase-percona/issues/1): Tables with foreign keys

### Version 1.1.0 (2014-11-06)

*   Initial version compatible with liquibase 3.3.0

### Version 1.0.0 (2014-10-09)

*   Initial version compatible with liquibase 3.2.0

## Using / Installing the extension

### Download

The jar files can be downloaded manually from maven:

<https://repo.maven.apache.org/maven2/org/liquibase/ext/liquibase-percona/>


### Command line liquibase

After extracting the zip file of liquibase, place `liquibase-percona-4.4.3.jar` file in the sub directory `lib`.
The shell script `liquibase` / `liquibase.bat` will automatically pick this up and the extension is available.

### Via Maven

Add the following dependency to the liquibase plugin:

```xml
<dependency>
    <groupId>org.liquibase.ext</groupId>
    <artifactId>liquibase-percona</artifactId>
    <version>4.4.3</version>
</dependency>
```

### Docker

You can also create a docker image which combines liquibase, liquibase-percona and percona toolkit.
The `Dockerfile` is located in the folder `docker` and is based on the official
[liquibase docker image](https://hub.docker.com/r/liquibase/liquibase).

In order to build the image locally run this command: `cd docker; docker build -t lb-percona .`

The you can simply run liquibase. The liquibase-percona extension and pt-online-schema-change
will automatically be picked up:

```
docker run --rm -v <PATH TO CHANGELOG DIR>:/liquibase/changelog lb-percona \
    --url="jdbc:mysql://<IP OR HOSTNAME>:3306/<DATABASE>" \
    --changeLogFile=com/example/changelog.xml \
    --username=<USERNAME> --password=<PASSWORD> \
    --logLevel=info \
    update
```

You can also run pt-online-schema-change directly, e.g.:

```
docker run --rm lb-percona /usr/local/bin/pt-online-schema-change \
    --alter-foreign-keys-method=auto --nocheck-unique-key-change \
    --alter="ADD COLUMN name VARCHAR(50) NOT NULL" \
    --password=<PASSWORD> \
    --dry-run --print \
    h=<IP OR HOSTNAME>,P=3306,u=<USERNAME>,D=<DATABASE>,t=<TABLE NAME>
```

### Using snapshots

Snapshot builds contain the latest features which are not yet available in a release.

Download: <https://oss.sonatype.org/content/repositories/snapshots/org/liquibase/ext/liquibase-percona/>

Enable the snapshot repository via Maven:

```xml
<project>
    <pluginRepositories>
        <pluginRepository>
            <id>sonatype-nexus-snapshots</id>
            <name>Sonatype Nexus Snapshots</name>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </pluginRepository>
    </pluginRepositories>
</project>
```

See also <https://maven.apache.org/guides/development/guide-testing-development-plugins.html>.

And just use the latest SNAPSHOT version for liquibase-percona dependency, e.g. `4.4.4-SNAPSHOT`:

```xml
<dependency>
    <groupId>org.liquibase.ext</groupId>
    <artifactId>liquibase-percona</artifactId>
    <version>4.4.4-SNAPSHOT</version>
</dependency>
```



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

Simply run `./mvnw clean verify`.
You'll find the jar-file in the `target/` subdirectory.


### Integration testing

In order to execute the integration tests, run `./mvnw clean verify -Prun-its`.

Please note, that you'll need:

1.  [docker](https://www.docker.com/).
    During the pre-integration-test phase the [official mysql image](https://hub.docker.com/_/mysql/) and
    [maria db image](https://hub.docker.com/_/mariadb/) will be started.
    Under debian, execute `sudo apt-get install docker.io`.
2.  Internet access to download the docker images the first time. And to download
    [percona toolkit](https://www.percona.com/downloads/percona-toolkit/). The build system will add the downloaded
    toolkit automatically to the `PATH`.
3.  The percona toolkit requires perl with mysql dbi libraries.
    Under debian, execute `sudo apt-get install libdbd-mysql-perl`.

See the properties *config_...* in `pom.xml` for connection details for the mysql docker instance.

To run a single integration test, execute maven like this: `./mvnw verify -Prun-its -Dinvoker.test=addColumn*,dropColumn`

## Common Problems

### NoSuchMethodError: PerconaDropColumnChange.getColumns()Ljava/util/List

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
* [Percona Toolkit Latest Documentation](https://www.percona.com/doc/percona-toolkit/LATEST/index.html)
* [Percona Toolkit on GitHub](https://github.com/percona/percona-toolkit)
* [Percona Toolkit 3.0 Documentation: pt-online-schema-change](https://www.percona.com/doc/percona-toolkit/3.0/pt-online-schema-change.html)
* [Percona Toolkit 2.2 Documentation: pt-online-schema-change](http://www.percona.com/doc/percona-toolkit/2.2/pt-online-schema-change.html)
* [Liquibase on GitHub](https://github.com/liquibase/liquibase)
* [Liquibase Percona Extension](https://liquibase.jira.com/wiki/display/CONTRIB/Percona+Online+Schema+Change)
* [DZone: Avoiding MySQL ALTER Table Downtime](http://java.dzone.com/articles/avoiding-mysql-alter-table)

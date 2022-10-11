# Liquibase Percona Extension

[![Build Status](https://github.com/liquibase/liquibase-percona/workflows/Build/badge.svg)](https://github.com/liquibase/liquibase-percona/actions)
[![Reproducible Builds](https://img.shields.io/badge/Reproducible_Builds-ok-green?labelColor=blue)](https://github.com/jvm-repo-rebuild/reproducible-central#org.liquibase.ext:liquibase-percona)
[![Maven Central](https://img.shields.io/maven-central/v/org.liquibase.ext/liquibase-percona)](https://search.maven.org/artifact/org.liquibase.ext/liquibase-percona)
[![Docker Image Version (tag latest semver)](https://img.shields.io/docker/v/andreasdangel/liquibase-percona/latest?label=docker%20version)](https://hub.docker.com/r/andreasdangel/liquibase-percona)

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
* Liquibase 4.0.0, 4.1.1, 4.2.2, 4.3.5, 4.4.3, 4.5.0, 4.6.2, 4.7.1, 4.8.0, 4.9.1, 4.10.0, 4.11.0, 4.12.0 (liquibase-percona 4.12.0). Percona Toolkit 3.3.1.
* Liquibase 4.0.0, 4.1.1, 4.2.2, 4.3.5, 4.4.3, 4.5.0, 4.6.2, 4.7.1, 4.8.0, 4.9.1, 4.10.0, 4.11.0, 4.12.0, 4.13.0, 4.14.0, 4.15.0 (liquibase-percona 4.15.0). Percona Toolkit 3.4.0.
* Liquibase 4.16.0 (liquibase-percona 4.16.0). Percona Toolkit 3.4.0.
* Liquibase 4.17.0 (liquibase-percona 4.17.0). Percona Toolkit 3.4.0.

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

See [CHANGELOG.md](CHANGELOG.md)

## Using / Installing the extension

### Download

The jar files can be downloaded manually from maven:

<https://repo.maven.apache.org/maven2/org/liquibase/ext/liquibase-percona/>


### Command line liquibase

After extracting the zip file of liquibase, place `liquibase-percona-4.17.0.jar` file in the sub directory `lib`.
The shell script `liquibase` / `liquibase.bat` will automatically pick this up and the extension is available.

### Via Maven

Add the following dependency to the liquibase plugin:

```xml
<dependency>
    <groupId>org.liquibase.ext</groupId>
    <artifactId>liquibase-percona</artifactId>
    <version>4.17.0</version>
</dependency>
```

### Docker

You can also create a docker image which combines liquibase, liquibase-percona and percona toolkit.
See [Liquibase Percona Docker images](https://hub.docker.com/r/andreasdangel/liquibase-percona).

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

And just use the latest SNAPSHOT version for liquibase-percona dependency, e.g. `4.17.1-SNAPSHOT`:

```xml
<dependency>
    <groupId>org.liquibase.ext</groupId>
    <artifactId>liquibase-percona</artifactId>
    <version>4.17.1-SNAPSHOT</version>
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

To run a single integration test, execute maven like this: `./mvnw verify -Prun-its -Dinvoker.test=sharedScripts,addColumn*,dropColumn`

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

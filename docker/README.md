# Liquibase Percona Docker images

This combines the [Official Liquibase Docker images](https://hub.docker.com/r/liquibase/liquibase)
with [Liquibase Percona Extension](https://github.com/liquibase/liquibase-percona) and
[Percona Toolkit](https://www.percona.com/doc/percona-toolkit/LATEST/index.html).

## Supported tags

*   [4.7.1, 4.7, latest](https://github.com/liquibase/liquibase-percona/blob/main/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.6.2, 4.6](https://github.com/liquibase/liquibase-percona/blob/d61bd176834250989584e709c60cb5001241c1f5/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.6.1.1](https://github.com/liquibase/liquibase-percona/blob/a50593d442a4cfa9285da1aaf4f9f5727246e9ed/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.6.1](https://github.com/liquibase/liquibase-percona/blob/b184630d6214a0261279fd320410577e1c4b9df4/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.5.0, 4.5](https://github.com/liquibase/liquibase-percona/blob/4475f925d7c93c28e5a6a9996718df681739064b/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.4.3, 4.4](https://github.com/liquibase/liquibase-percona/blob/11761c13726b84cba7f234689294238078337fba/docker/Dockerfile) (Percona Toolkit 3.3.1)

## Usage

Execute a database update by simply running liquibase.
The liquibase-percona extension and pt-online-schema-change
will automatically be picked up:

```
docker run --rm -v <PATH TO CHANGELOG DIR>:/liquibase/changelog andreasdangel/liquibase-percona \
    --url="jdbc:mysql://<IP OR HOSTNAME>:3306/<DATABASE>" \
    --changeLogFile=com/example/changelog.xml \
    --username=<USERNAME> --password=<PASSWORD> \
    --logLevel=info \
    update
```

You can also run pt-online-schema-change directly, e.g.:

```
docker run --rm andreasdangel/liquibase-percona /usr/local/bin/pt-online-schema-change \
    --alter-foreign-keys-method=auto --nocheck-unique-key-change \
    --alter="ADD COLUMN name VARCHAR(50) NOT NULL" \
    --password=<PASSWORD> \
    --dry-run --print \
    h=<IP OR HOSTNAME>,P=3306,u=<USERNAME>,D=<DATABASE>,t=<TABLE NAME>
```

## Source

*   <https://github.com/liquibase/liquibase-percona/tree/main/docker>


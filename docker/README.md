# Liquibase Percona Docker images

This combines the [Official Liquibase Docker images](https://hub.docker.com/r/liquibase/liquibase)
with [Liquibase Percona Extension](https://github.com/liquibase/liquibase-percona) and
[Percona Toolkit](https://www.percona.com/doc/percona-toolkit/LATEST/index.html).

## Supported tags

*   [4.30.0, 4.30, latest](https://github.com/liquibase/liquibase-percona/blob/main/docker/Dockerfile) (Percona Toolkit 3.6.0)
*   [4.29.2, 4.29](https://github.com/liquibase/liquibase-percona/blob/465cb21da46db1c9a3d15b3dc084434e17c57f24/docker/Dockerfile) (Percona Toolkit 3.6.0)
*   [4.28.0, 4.28](https://github.com/liquibase/liquibase-percona/blob/4924b4fe1670909d01b409e752cdc7210884ff59/docker/Dockerfile) (Percona Toolkit 3.5.7)
*   [4.27.0, 4.27](https://github.com/liquibase/liquibase-percona/blob/ec56085fc71f988ca2ac29fdaf51897d190a4bf0/docker/Dockerfile) (Percona Toolkit 3.5.7)
*   [4.26.0, 4.26](https://github.com/liquibase/liquibase-percona/blob/4d5938c8f82578de5e710c363040837229b5e8d1/docker/Dockerfile) (Percona Toolkit 3.5.5)
*   [4.25.0, 4.25](https://github.com/liquibase/liquibase-percona/blob/9248833e77cab5925ac0ae3872e3aba7b2cd0bc3/docker/Dockerfile) (Percona Toolkit 3.5.5)
*   [4.24.0, 4.24](https://github.com/liquibase/liquibase-percona/blob/2a3b1eedd9cda15ebc7319c3d21959ccaeab8f17/docker/Dockerfile) (Percona Toolkit 3.5.5)
*   [4.23.2, 4.23](https://github.com/liquibase/liquibase-percona/blob/b2c15337500a3c0caba65e0e807902a12fcb8451/docker/Dockerfile) (Percona Toolkit 3.5.4)
*   [4.23.1](https://github.com/liquibase/liquibase-percona/blob/23416ba64189b059358bafe1f7ef97c4f55752bf/docker/Dockerfile) (Percona Toolkit 3.5.4)
*   [4.23.0](https://github.com/liquibase/liquibase-percona/blob/53a58d732508240b969bf4d6eb3296673d07c7c8/docker/Dockerfile) (Percona Toolkit 3.5.3)
*   [4.22.0, 4.22](https://github.com/liquibase/liquibase-percona/blob/9e136f306f3c4aa69c816fe1ab0d3891ee191083/docker/Dockerfile) (Percona Toolkit 3.5.2)
*   [4.20.0, 4.20](https://github.com/liquibase/liquibase-percona/blob/772d07ca50d513affd7affea925ef878d5604b0d/docker/Dockerfile) (Percona Toolkit 3.5.1)
*   [4.19.1, 4.19](https://github.com/liquibase/liquibase-percona/blob/0bd5e39b833b54b27695af8f410a08f7c784e70b/docker/Dockerfile) (Percona Toolkit 3.5.1)
*   [4.19.0](https://github.com/liquibase/liquibase-percona/blob/a63f959050334888fa6ba42681ddc2381a94bc09/docker/Dockerfile) (Percona Toolkit 3.5.0)
*   [4.18.0, 4.18](https://github.com/liquibase/liquibase-percona/blob/d59b9f8b6c0e0c296b2690bcf990803d3e996e08/docker/Dockerfile) (Percona Toolkit 3.5.0)
*   [4.17.1, 4.17](https://github.com/liquibase/liquibase-percona/blob/32972ba9573e9dd7863ce4320948fdfd89348940/docker/Dockerfile) (Percona Toolkit 3.4.0)
*   [4.17.0](https://github.com/liquibase/liquibase-percona/blob/ad87992a358313da6cb7ea47cd88562b58e2f496/docker/Dockerfile) (Percona Toolkit 3.4.0)
*   [4.16.0, 4.16](https://github.com/liquibase/liquibase-percona/blob/068b49992825ed2f16f8b171736b8f989c829e2c/docker/Dockerfile) (Percona Toolkit 3.4.0)
*   [4.15.0, 4.15](https://github.com/liquibase/liquibase-percona/blob/21d1cf6f7515692b12994eda8ac3b7343fdad38b/docker/Dockerfile) (Percona Toolkit 3.4.0)
*   [4.14.0, 4.14](https://github.com/liquibase/liquibase-percona/blob/ecdff8a42b94c5f9baadf5d9c6a5c5967f942488/docker/Dockerfile) (Percona Toolkit 3.4.0)
*   [4.13.0, 4.13](https://github.com/liquibase/liquibase-percona/blob/8a28dd673574b781278e6d49be3fd36a5a26fd7b/docker/Dockerfile) (Percona Toolkit 3.4.0)
*   [4.12.0, 4.12](https://github.com/liquibase/liquibase-percona/blob/8604bde698ebb8bbbb08db04f2f394cebb51553c/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.11.0, 4.11](https://github.com/liquibase/liquibase-percona/blob/7e5aeb5a521ce82a2eb6fdbde8f62be7ed6e583f/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.10.0, 4.10](https://github.com/liquibase/liquibase-percona/blob/da116adadd192d49fc170d7d31c4305c81a26ca4/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.9.1, 4.9](https://github.com/liquibase/liquibase-percona/blob/f1de6ad0e281b4db8fde59026b7fecb51b28e984/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.9.0](https://github.com/liquibase/liquibase-percona/blob/f5a0080b2ec44294771a82f9cf97daad513e1f6a/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.8.0, 4.8](https://github.com/liquibase/liquibase-percona/blob/5f50f6f6a861357ee499261db7c60cc54393b458/docker/Dockerfile) (Percona Toolkit 3.3.1)
*   [4.7.1, 4.7](https://github.com/liquibase/liquibase-percona/blob/3e320741e56d272f9ce16aeaebcbb013343785f0/docker/Dockerfile) (Percona Toolkit 3.3.1)
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
docker run --rm -v <PATH TO CHANGELOG DIR>:/liquibase/changelog \
    andreasdangel/liquibase-percona \
    --url="jdbc:mysql://<IP OR HOSTNAME>:3306/<DATABASE>" \
    --changeLogFile=com/example/changelog.xml \
    --username=<USERNAME> --password=<PASSWORD> \
    --logLevel=info \
    update
```

If you want to set a [system property](https://github.com/liquibase/liquibase-percona#system-properties),
e.g. `liquibase.percona.defaultOn=false`, you need to use "**-e JAVA_OPTS**".

```
docker run --rm -v <PATH TO CHANGELOG DIR>:/liquibase/changelog \
    -e JAVA_OPTS=-Dliquibase.percona.defaultOn=false \
    andreasdangel/liquibase-percona \
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


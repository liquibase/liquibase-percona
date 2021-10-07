FROM liquibase/liquibase:4.5.0

RUN lpm add mysql --global

USER root
RUN apt-get update \
    && apt-get -y install perl libdbd-mysql-perl \
    && rm -rf /var/lib/apt/lists/*

RUN ln -s /liquibase/percona-toolkit/bin/pt-online-schema-change /usr/local/bin/pt-online-schema-change

USER liquibase

# Download liquibase-percona extension
ARG LB_PERCONA_VERSION=4.5.0
ARG LB_PERCONA_SHA256=cda0ebcbde11f1f761e7d5289e33f1fcca7b4090b08efbd36462a2bb593d7024
RUN wget --no-verbose -O /liquibase/lib/liquibase-percona.jar https://repo.maven.apache.org/maven2/org/liquibase/ext/liquibase-percona/${LB_PERCONA_VERSION}/liquibase-percona-${LB_PERCONA_VERSION}.jar \
#    && wget --no-verbose -O /liquibase/lib/liquibase-percona.jar.asc https://repo.maven.apache.org/maven2/org/liquibase/ext/liquibase-percona/${LB_PERCONA_VERSION}/liquibase-percona-${LB_PERCONA_VERSION}.jar.asc \
#    && gpg --auto-key-locate keyserver --keyserver ha.pool.sks-keyservers.net --keyserver-options auto-key-retrieve --verify /liquibase/lib/liquibase-percona.jar.asc /liquibase/lib/liquibase-percona.jar \
    && echo "$LB_PERCONA_SHA256  /liquibase/lib/liquibase-percona.jar" | sha256sum -c - 

# Download percona toolkit
ARG PERCONA_TOOLKIT_VERSION=3.3.1
ARG PERCONA_TOOLKIT_SHA256=60fc106b195b6716f1ebb3ca16b401692228c1a2a885da72111a93391fd12090
RUN wget --no-verbose -O /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz https://downloads.percona.com/downloads/percona-toolkit/${PERCONA_TOOLKIT_VERSION}/source/tarball/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz \
    && echo "$PERCONA_TOOLKIT_SHA256  /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz" | sha256sum -c - \
    && tar -C /liquibase -xzf percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz \
    && mv /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION} /liquibase/percona-toolkit \
    && rm /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz

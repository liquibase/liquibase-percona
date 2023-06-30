ARG LIQUIBASE_VERSION=4.22.0
ARG LB_PERCONA_VERSION=4.22.0
ARG PERCONA_TOOLKIT_VERSION=3.5.3

FROM liquibase/liquibase:${LIQUIBASE_VERSION}

RUN lpm add mysql --global

USER root
RUN apt-get update \
    && apt-get -y install gpg perl libdbd-mysql-perl curl \
    && rm -rf /var/lib/apt/lists/*

RUN ln -s /liquibase/percona-toolkit/bin/pt-online-schema-change /usr/local/bin/pt-online-schema-change

USER liquibase
SHELL ["/bin/bash", "-c"]

# Download liquibase-percona extension
# Signed with: support@liquibase.org - http://keyserver.ubuntu.com/pks/lookup?search=0xBAF4CE3CC55E6523CF5881A78AFE608B21FE903C&fingerprint=on&op=index
ARG LB_PERCONA_VERSION
RUN wget --no-verbose -O /liquibase/lib/liquibase-percona.jar https://repo.maven.apache.org/maven2/org/liquibase/ext/liquibase-percona/${LB_PERCONA_VERSION}/liquibase-percona-${LB_PERCONA_VERSION}.jar \
    && wget --no-verbose -O /liquibase/lib/liquibase-percona.jar.asc https://repo.maven.apache.org/maven2/org/liquibase/ext/liquibase-percona/${LB_PERCONA_VERSION}/liquibase-percona-${LB_PERCONA_VERSION}.jar.asc \
    && gpg --auto-key-locate keyserver --keyserver keyserver.ubuntu.com --keyserver-options auto-key-retrieve --verify /liquibase/lib/liquibase-percona.jar.asc /liquibase/lib/liquibase-percona.jar \
    && LB_PERCONA_SHA1="$(curl -s https://repo.maven.apache.org/maven2/org/liquibase/ext/liquibase-percona/${LB_PERCONA_VERSION}/liquibase-percona-${LB_PERCONA_VERSION}.jar.sha1)" \
    && echo "$LB_PERCONA_SHA1  /liquibase/lib/liquibase-percona.jar" | sha1sum -c -

# Download percona toolkit
ARG PERCONA_TOOLKIT_VERSION
RUN wget --no-verbose -O /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz https://downloads.percona.com/downloads/percona-toolkit/${PERCONA_TOOLKIT_VERSION}/source/tarball/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz \
    && PERCONA_TOOLKIT_SHA256="$(curl -s https://downloads.percona.com/downloads/percona-toolkit/${PERCONA_TOOLKIT_VERSION}/source/tarball/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz.sha256sum)" \
    && echo "${PERCONA_TOOLKIT_SHA256:0:64}  /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz" | sha256sum -c - \
    && tar -C /liquibase -xzf percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz \
    && mv /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION} /liquibase/percona-toolkit \
    && rm /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz

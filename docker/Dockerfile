ARG LIQUIBASE_VERSION=5.0.4
ARG LB_PERCONA_VERSION=5.0.4
# https://www.percona.com/downloads/
# https://downloads.percona.com/downloads/percona-toolkit/3.7.1-4/binary/tarball/percona-toolkit-3.7.1_x86_64.tar.gz
ARG PERCONA_TOOLKIT_VERSION=3.7.1-4
ARG PERCONA_TOOLKIT_VERSION_SHORT=3.7.1
ARG PERCONA_TOOLKIT_SHA256="478d9ad91c013597f6ec2df05e63f17db5a65b8507bef695b86fa876f582e69d  percona-toolkit-3.7.1_x86_64.tar.gz"

FROM liquibase/liquibase:${LIQUIBASE_VERSION}

RUN lpm add mysql --global

USER root
RUN apt-get update \
    && apt-get -y install gpg perl libdbd-mysql-perl curl \
    && rm -rf /var/lib/apt/lists/*

RUN ln -s /liquibase/percona-toolkit/bin/pt-online-schema-change /usr/local/bin/pt-online-schema-change \
    && mkdir -p /home/liquibase/.gnupg \
    && chown -R liquibase /home/liquibase

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
ARG PERCONA_TOOLKIT_VERSION_SHORT
ARG PERCONA_TOOLKIT_SHA256
RUN wget --no-verbose -O /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz https://downloads.percona.com/downloads/percona-toolkit/${PERCONA_TOOLKIT_VERSION}/binary/tarball/percona-toolkit-${PERCONA_TOOLKIT_VERSION_SHORT}_x86_64.tar.gz \
    #&& PERCONA_TOOLKIT_SHA256="$(curl -s https://downloads.percona.com/downloads/percona-toolkit/${PERCONA_TOOLKIT_VERSION}/binary/tarball/percona-toolkit-${PERCONA_TOOLKIT_VERSION_SHORT}_x86_64.tar.gz.sha256sum)" \
    && echo "${PERCONA_TOOLKIT_SHA256:0:64}  /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz" | sha256sum -c - \
    && tar -C /liquibase -xzf percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz \
    && mv /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION_SHORT} /liquibase/percona-toolkit \
    && rm /liquibase/percona-toolkit-${PERCONA_TOOLKIT_VERSION}.tar.gz

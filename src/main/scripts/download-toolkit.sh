#!/usr/bin/env bash

#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

VERSION=$1

if [ -z "${VERSION}" ]; then
    echo "$0 <version>" 2>&1
    echo 2>&1
    echo "Please specify the version to download!" 2>&1
    exit 1
fi

url=https://downloads.percona.com/downloads/percona-toolkit/${VERSION}/source/tarball/percona-toolkit-${VERSION}.tar.gz
# the tags on github are unfortunately wrong and can't be used to download the source...
#url=https://github.com/percona/percona-toolkit/archive/v${VERSION}.tar.gz

filename=percona-toolkit-${VERSION}.tar.gz
target=percona-toolkit-${VERSION}

if [ -e "${filename}" ]; then
  echo "Skipping download ${filename}..."
else
  echo "Downloading ${filename}..."
  curl \
    --location \
    --output ${filename} \
    ${url}
  if [ $? -ne 0 ]; then
    echo "Download from ${url} failed..."
    exit 1
  fi
fi

echo "Extracting..."
tar xfz ${filename}

if [ ! -d ${target} ]; then
  echo "The directory ${target} doesn't exist - something went wrong!"
  exit 1
fi

downloaded_version=$(tail -3 ${target}/bin/pt-online-schema-change |head -1)
if [ "${downloaded_version}" != "pt-online-schema-change ${VERSION}" ]; then
  echo "Wrong version downloaded: '${downloaded_version}' but expected to be '${VERSION}'"
  exit 1
fi

echo "percona toolkit is available at: $(realpath ${target})"

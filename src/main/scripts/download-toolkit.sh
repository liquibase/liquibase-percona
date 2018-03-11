#!/bin/bash

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


VERSION=$1

if [ -z "${VERSION}" ]; then
    echo "$0 <version>" 2>&1
    echo 2>&1
    echo "Please specify the version to download!" 2>&1
    exit 1
fi

function get() {
    filename=$1
    subdir=$2
    echo "Downloading $filename..."
    if [ -s $filename ]; then
        tar xfz $filename
        return 0
    fi

    wget -q https://www.percona.com/downloads/percona-toolkit/${VERSION}/${subdir}tarball/$filename \
        -O $filename && \
        tar xfz $filename
}


# first try with hardware
get percona-toolkit-${VERSION}_x86_64.tar.gz binary/
if [ $? != 0 ]; then
    # try without hardware
    get percona-toolkit-${VERSION}.tar.gz binary/
fi
if [ $? != 0 ]; then
    # try lastly without subdir
    get percona-toolkit-${VERSION}.tar.gz
fi


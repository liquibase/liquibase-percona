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

# percona way:
curl \
  --silent \
  --output LATEST.html \
  https://www.percona.com/downloads/percona-toolkit/LATEST/

VERSION=$(grep 'selected="selected">Percona' LATEST.html | sed 's/.*\([0-9][0-9]*.[0-9][0-9]*.[0-9][0-9]*\).*/\1/')
echo $VERSION

# github way:
# ask github for the tags, use the first tag - hope, it is the latest
#
#VERSION=$(curl \
#  --silent \
#  --header "Accept: application/vnd.github.v3+json" \
#  https://api.github.com/repos/percona/percona-toolkit/tags \
#  | jq ".[0].name" --raw-output)
#
# output version, removing potential "v" prefix from version
#echo ${VERSION##v}

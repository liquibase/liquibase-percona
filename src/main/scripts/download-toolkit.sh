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

function main()
{
    if [ $# -eq 0 ] || [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        (
            echo "$0 <version> [<version>...]"
            echo
            echo "Please specify the versions to download!"
            echo
            echo "Environment Variables:"
            echo "TARGET - base target directory, where percona toolkit versions should be extracted to"
            echo "USE_CACHE - true or false"
            echo "CACHE_DIR - caches previously downloaded percona toolkit versions"
        ) >&2
        exit 1
    fi

    if [ -z "${TARGET}" ]; then
        echo "Environment variable TARGET is missing!" >&2
        exit 1
    fi

    echo "Percona Toolkit Downloader"
    echo "--------------------------"
    echo "Target Directory: ${TARGET}"
    if [ "${USE_CACHE,,}" = "true" ]; then
        USE_CACHE=yes
        if [ -z "${CACHE_DIR}" ]; then
            echo "Environment variable CACHE_DIR is missing!" >&1
            exit 1
        fi
        echo "Cache: ${USE_CACHE}"
        if [ ! -d "${CACHE_DIR}" ]; then
            mkdir "${CACHE_DIR}"
            echo "Created cache directory: ${CACHE_DIR}"
        else
            echo "Using cache directory: ${CACHE_DIR}"
        fi
    else
        USE_CACHE=no
        echo "Cache: ${USE_CACHE}"
    fi

    if [ -f "${TARGET}" ]; then
        echo "Directory ${TARGET} is a file!" >&2
        exit 1
    fi

    if [ ! -d "${TARGET}" ]; then
        mkdir "${TARGET}"
        echo "Created directory ${TARGET}"
    fi

    for version in "$@"; do
        if [ "${version,,}" = "latest" ]; then
            real_version=$(determine_latest_toolkit)
        else
            real_version="${version}"
        fi

        echo
        echo "Downloading ${real_version}..."
        download_toolkit "${real_version}"
        extract_toolkit "${real_version}" "${version}"
    done
}

function is_cache_enabled() { [ "${USE_CACHE}" = "yes" ]; }
function determine_latest_toolkit()
{
    local cached_version="${CACHE_DIR}/latest-version.txt"

    if is_cache_enabled
    then
        if [ -e "${cached_version}" ]
        then
            one_day_ago=$(date -d 'now - 1 day' +%s)
            file_time=$(date -r "${cached_version}" +%s)
            if (( file_time > one_day_ago ))
            then
                cat "${cached_version}"
                return
            fi
        fi
    fi

    echo "Determining latest version..." >&2
    local release_notes
    release_notes=$(curl --silent https://docs.percona.com/percona-toolkit/release_notes.html)
    local all_versions
    all_versions=$(echo "$release_notes"|grep -i '<h2 id="v'|sed 's/.*v\([0-9.-]\+\) released.*/\1/')

    # find the first version, that is available for download. Not all versions might exist yet for download.
    local version
    local latest_version=""
    for version in $all_versions; do
      echo -n " checking version $version for availability..." >&2
      if curl --head --fail --silent "https://downloads.percona.com/downloads/percona-toolkit/${version}/binary/tarball/percona-toolkit-${version}_x86_64.tar.gz" >/dev/null; then
        if curl --head --fail --silent "https://downloads.percona.com/downloads/percona-toolkit/${version}/binary/tarball/percona-toolkit-${version}_x86_64.tar.gz.sha256sum" >/dev/null; then
          echo " OK" >&2
          latest_version="$version"
          break
        fi
        echo " missing sha256sum" >&2
      else
        echo " missing tar.gz" >&2
      fi
    done

    if [ "$latest_version" = "" ]; then
      echo "Couldn't determine latest toolkit version!" >&2
      exit 1
    fi

    echo "Determined latest available version as: ${latest_version}" >&2

    if is_cache_enabled
    then
        echo "$latest_version" > "$cached_version"
    fi
    echo "$latest_version"
}

function download_toolkit()
{
    local version="$1"
    local filename="percona-toolkit-${version}.tar.gz"
    local cached_file="${CACHE_DIR}/${filename}"

    if is_cache_enabled
    then
        if [ -e "${cached_file}" ]; then
            echo "${filename} already exists in ${CACHE_DIR}, not downloading" >&2
            cp "${cached_file}" "${TARGET}/${filename}"
            return
        fi
    fi

    local url="https://downloads.percona.com/downloads/percona-toolkit/${version}/binary/tarball/percona-toolkit-${version}_x86_64.tar.gz"
    echo "Downloading ${filename} from ${url}..." >&2
    if ! curl \
      --location \
      --output "${TARGET}/${filename}" \
        "${url}"; then
        echo "Download from ${url} failed..." >&2
        exit 1
    fi

    if is_cache_enabled
    then
        echo "Caching ${filename}" >&2
        cp "${TARGET}/${filename}" "${cached_file}"
    fi
}

function extract_toolkit() {
    local version="$1"
    local name="$2"
    local filename="percona-toolkit-${version}.tar.gz"
    local cached_file="${CACHE_DIR}/${filename}"
    local extract_target_dir="percona-toolkit-${version}"
    local extract_target_dir2="percona-toolkit-${name}"

    echo "Extracting..." >&2
    (
        cd "${TARGET}"
        rm -rf "${extract_target_dir}"
        tar xfz "${filename}"

        if [ ! -d "${extract_target_dir}" ]; then
            echo "The directory ${extract_target_dir} doesn't exist - something went wrong!" >&2
            exit 1
        fi

        local downloaded_version
        downloaded_version=$(tail -3 "${extract_target_dir}/bin/pt-online-schema-change" |head -1)
        if [ "${downloaded_version}" != "pt-online-schema-change ${version}" ]; then
          echo "Wrong version downloaded: '${downloaded_version}' but expected to be '${version}'" >&2
          exit 1
        fi

        if [ "${extract_target_dir}" != "${extract_target_dir2}" ]; then
            echo "Renaming to ${extract_target_dir2}" >&2
            rm -rf "${extract_target_dir2}"
            mv "${extract_target_dir}" "${extract_target_dir2}"
            extract_target_dir="${extract_target_dir2}"
        fi

        echo "--> Percona Toolkit ${name} is available at: $(realpath "${extract_target_dir}")" >&2
    )
}

main "$@"

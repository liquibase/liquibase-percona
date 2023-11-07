package liquibase.ext.percona;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public final class PerconaToolkitVersion {

    private final int major;
    private final int minor;
    private final int patch;

    public PerconaToolkitVersion(String version) {
        if (version == null) {
            major = 0;
            minor = 0;
            patch = 0;
        } else {
            String[] parts = version.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Invalid Version Format: " + version);
            major = Integer.parseInt(parts[0]);
            minor = Integer.parseInt(parts[1]);
            patch = Integer.parseInt(parts[2]);
        }
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public boolean isGreaterOrEqualThan(String otherVersion) {
        PerconaToolkitVersion other = new PerconaToolkitVersion(otherVersion);

        if (major > other.major) {
            return true;
        } else if (major == other.major && minor > other.minor) {
            return true;
        } else if (major == other.major && minor == other.minor && patch >= other.patch) {
            return true;
        }

        return false;
    }
}


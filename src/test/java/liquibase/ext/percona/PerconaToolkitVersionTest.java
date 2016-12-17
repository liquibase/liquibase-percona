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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PerconaToolkitVersionTest {

    @Test
    public void simpleParse() {
        PerconaToolkitVersion version = new PerconaToolkitVersion("2.2.17");
        assertEquals("2.2.17", version.toString());
    }

    @Test
    public void checkGreaterOrEqual() {
        PerconaToolkitVersion version = new PerconaToolkitVersion("2.2.17");
        assertTrue(version.isGreaterOrEqualThan("2.2.17"));
        assertTrue(version.isGreaterOrEqualThan("2.2.15"));
        assertTrue(version.isGreaterOrEqualThan("2.1.15"));
        assertTrue(version.isGreaterOrEqualThan("1.1.15"));
        assertFalse(version.isGreaterOrEqualThan("2.2.18"));
        assertFalse(version.isGreaterOrEqualThan("2.2.20"));
        assertFalse(version.isGreaterOrEqualThan("3.0.0"));
    }
}

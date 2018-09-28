package liquibase.ext.percona;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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

public class PerconaForeignKeyServiceTest {
    @BeforeClass
    public static void init() {
        PerconaForeignKeyService.getInstance().disable();
    }

    @Test
    public void testPrefixing() {
        PerconaForeignKeyService service = PerconaForeignKeyService.getInstance();
        Assert.assertEquals("_FK_1", service.determineCurrentConstraintName(null, createChange("table", "FK_1")));
        Assert.assertEquals("__FK_1", service.determineCurrentConstraintName(null, createChange("table", "_FK_1")));
        Assert.assertEquals("FK_1", service.determineCurrentConstraintName(null, createChange("table", "__FK_1")));
    }

    private static PerconaDropForeignKeyConstraintChange createChange(String baseTableName, String constraintName) {
        PerconaDropForeignKeyConstraintChange change = new PerconaDropForeignKeyConstraintChange();
        change.setBaseTableName(baseTableName);
        change.setConstraintName(constraintName);
        return change;
    }
}

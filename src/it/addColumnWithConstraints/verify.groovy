import java.sql.ResultSet;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

File buildLog = new File( basedir, 'build.log' )
assert buildLog.exists()
def buildLogText = buildLog.text;

assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::3::Alice: Executing: pt-online-schema-change --alter=\"ADD COLUMN testColumn BIGINT NULL, ADD CONSTRAINT fk_test_column FOREIGN KEY (testColumn) REFERENCES test_parent(id)\"")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::3::Alice: Successfully altered `testdb`.`test_table`.")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::3::Alice: Columns testColumn(bigint(20)) added to test_table")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::4::Alice: Executing: pt-online-schema-change --alter=\"ADD COLUMN testColumn2 BIGINT NULL, ADD CONSTRAINT uc_test_column UNIQUE (testColumn2)\"")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::4::Alice: Successfully altered `testdb`.`test_table`.")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::4::Alice: Columns testColumn2(bigint(20)) added to test_table")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::5::Alice: Executing: pt-online-schema-change --alter=\"ADD COLUMN testColumn3 BIGINT NULL, ADD CONSTRAINT fk_test_column3 FOREIGN KEY (testColumn3) REFERENCES test_parent(id), ADD CONSTRAINT uc_test_column3 UNIQUE (testColumn3)\"")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::5::Alice: Successfully altered `testdb`.`test_table`.")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::5::Alice: Columns testColumn3(bigint(20)) added to test_table")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::6::Alice: Executing: pt-online-schema-change --alter=\"ADD COLUMN testColumn4 BIGINT NULL, ADD CONSTRAINT fk_test_column4 FOREIGN KEY (testColumn4) REFERENCES test_parent(id)\"")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::6::Alice: Successfully altered `testdb`.`test_table`.")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::6::Alice: Columns testColumn4(bigint(20)) added to test_table")

File sql = new File( basedir, 'target/liquibase/migrate.sql' )
assert sql.exists()
def sqlText = sql.text;
assert sqlText.contains("pt-online-schema-change")
assert !sqlText.contains("password=${config_password}")

def con, s;
try {
    def props = new Properties();
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new com.mysql.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}/${config_dbname}", props)
    s = con.createStatement();
    r = s.executeQuery("SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA='${config_dbname}' AND TABLE_NAME='test_table' ORDER BY CONSTRAINT_NAME ASC")
    assert r.first()
    assertColumn(r, "fk_test_column", "FOREIGN KEY")
    assert r.next()
    assertColumn(r, "fk_test_column4", "FOREIGN KEY")
    assert r.next()
    assertColumn(r, "PRIMARY", "PRIMARY KEY")
    assert r.next()
    assertColumn(r, "uc_test_column", "UNIQUE")
    assert r.next()
    assertColumn(r, "uc_test_column3", "UNIQUE")
    assert r.next()
    assertColumn(r, "_fk_test_column3", "FOREIGN KEY")
    r.close()
} finally {
    s?.close();
    con?.close();
}

def assertColumn(resultset, name, type) {
    assert name == resultset.getString(1)
    assert type == resultset.getString(2)
}

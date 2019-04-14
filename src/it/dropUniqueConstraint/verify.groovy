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
assert buildLogText.contains("test-changelog.xml::3::Alice: Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP KEY uq_id_name\" --host=127.0.0.1 --port=${config_port} --user=${config_user} --password=*** --execute D=${config_dbname},t=person");
assert buildLogText.contains("test-changelog.xml::3::Alice: Altering `${config_dbname}`.`person`...");
assert buildLogText.contains("test-changelog.xml::3::Alice: Creating new table...");
assert buildLogText.contains("test-changelog.xml::3::Alice: Created new table ${config_dbname}._person_new OK.");
assert buildLogText.contains("test-changelog.xml::3::Alice: Altering new table...");
assert buildLogText.contains("test-changelog.xml::3::Alice: Altered `${config_dbname}`.`_person_new` OK.");
assert buildLogText.contains("test-changelog.xml::3::Alice: Successfully altered `${config_dbname}`.`person`.");
assert buildLogText.contains("test-changelog.xml::3::Alice: Unique constraint uq_id_name dropped from person");

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
    r = s.executeQuery("SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA='${config_dbname}' AND TABLE_NAME='person' AND CONSTRAINT_TYPE='UNIQUE'")
    assert !r.first()
    r.close()
} finally {
    s?.close();
    con?.close();
}

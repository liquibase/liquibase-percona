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
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::2::Alice: Executing: pt-online-schema-change --alter=\"ADD COLUMN address VARCHAR(255) NULL\" --alter-foreign-keys-method=auto --nocheck-unique-key-change --host=${config_host} --port=${config_port} --user=${config_user} --password=*** --execute D=testdb,t=person")
assert buildLogText.contains("ChangeSet test-changelog.xml::2::Alice ran successfully")
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::4::Alice: Executing: pt-online-schema-change --alter=\"ADD COLUMN address VARCHAR(255) NULL\" --alter-foreign-keys-method=auto --nocheck-unique-key-change --host=${config_host} --port=${config_port} --user=${config_user} --password=*** --execute D=testdb_cross,t=person")
assert buildLogText.contains("ChangeSet test-changelog.xml::4::Alice ran successfully")

def con, s;
// check database ${config_dbname}
try {
    def props = new Properties();
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new com.mysql.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}/${config_dbname}", props)
    s = con.createStatement();
    r = s.executeQuery("DESCRIBE person")
    assert r.first()
    assertColumn(r, "name", "varchar(255)", "NO", null)
    assert r.next()
    assertColumn(r, "address", "varchar(255)", "YES", null)
    r.close()
} finally {
    s?.close();
    con?.close();
}

// check database ${config_dbname}_cross
try {
    def props = new Properties();
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new com.mysql.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}/${config_dbname}_cross", props)
    s = con.createStatement();
    r = s.executeQuery("DESCRIBE person")
    assert r.first()
    assertColumn(r, "name", "varchar(255)", "NO", null)
    assert r.next()
    assertColumn(r, "address", "varchar(255)", "YES", null)
    r.close()
} finally {
    s?.close();
    con?.close();
}

def assertColumn(resultset, name, type, nullable, defaultValue) {
    assert name == resultset.getString(1)
    assert type == resultset.getString(2)
    assert nullable == resultset.getString(3)
    assert defaultValue == resultset.getString(5)
}

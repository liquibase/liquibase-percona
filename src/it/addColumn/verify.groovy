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
assert buildLogText.contains("ChangeSet test-changelog.xml::3::Alice ran successfully")
assert buildLogText.contains("Columns address(varchar(255)) added to person");
assert buildLogText.contains("Altering `testdb`.`person`...")
assert buildLogText.contains("Successfully altered `testdb`.`person`.")
assert buildLogText.contains("Columns email(varchar(255)),age(int) added to person")

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
    con = new com.mysql.cj.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}/${config_dbname}?useSSL=false&allowPublicKeyRetrieval=true", props)
    s = con.createStatement();
    r = s.executeQuery("DESCRIBE person")
    assert r.next()
    assertColumn(r, "name", "varchar(255)", "NO", null)
    assert r.next()
    assertColumn(r, "address", "varchar(255)", "YES", null)
    assert r.next()
    assertColumn(r, "email", "varchar(255)", "YES", null)
    assert r.next()
    assertColumn(r, "age", "int(11)", "YES", null)
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

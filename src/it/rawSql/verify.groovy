import java.sql.ResultSet
import java.sql.Statement
import com.mysql.cj.jdbc.Driver

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
def buildLogText = buildLog.text
assert buildLogText.contains("Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"ADD COLUMN address VARCHAR(255) NULL\" --password=*** --execute h=${config_host},P=${config_port},u=${config_user},D=testdb,t=person")
assert buildLogText.contains("ChangeSet test-changelog.xml::2::Alice ran successfully")
assert buildLogText.contains("Custom SQL executed")
assert buildLogText.contains("Altering `testdb`.`person`...")
assert buildLogText.contains("Successfully altered `testdb`.`person`.")
assert buildLogText.contains("Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"ADD COLUMN email VARCHAR(255) NULL, ADD COLUMN `age` INT NULL\" --password=*** --execute h=${config_host},P=${config_port},u=${config_user},D=testdb,t=person")
assert buildLogText.contains("ChangeSet test-changelog.xml::3::Alice ran successfully")

File sql = new File( basedir, 'target/liquibase/migrate.sql' )
assert sql.exists()
def sqlText = sql.text
assert sqlText.contains("pt-online-schema-change")
assert !sqlText.contains("password=${config_password}")

def con = null
Statement s = null
try {
    def props = new Properties()
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new Driver().connect("jdbc:mysql://${config_host}:${config_port}/${config_dbname}?useSSL=false&allowPublicKeyRetrieval=true", props)
    s = con.createStatement()
    ResultSet r = s.executeQuery("DESCRIBE person")
    assert r.next()
    assertColumn(r, "name", "varchar(255)", "NO", null)
    assert r.next()
    assertColumn(r, "address", "varchar(255)", "YES", null)
    assert r.next()
    assertColumn(r, "email", "varchar(255)", "YES", null)
    assert r.next()
    // for MySQL 5.7, the type is int(11), for MySQL 8, it is int
    assertColumn(r, "age", "int", "YES", null)
    r.close()
} finally {
    s?.close()
    con?.close()
}

static def assertColumn(resultset, name, type, nullable, defaultValue) {
    assert name == resultset.getString(1)
    assert resultset.getString(2).contains(type)
    assert nullable == resultset.getString(3)
    assert defaultValue == resultset.getString(5)
}

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
assert buildLogText.contains("liquibase: test-changelog.xml: test-changelog.xml::3::Alice: Executing: pt-online-schema-change --alter=\"DROP INDEX emailIdx\" --alter-foreign-keys-method=auto --host=${config_host} --port=${config_port} --user=${config_user} --password=*** --execute D=testdb,t=person")

File sql = new File( basedir, 'target/liquibase/migrate.sql' )
assert sql.exists()
def sqlText = sql.text;
assert sqlText.contains("pt-online-schema-change --alter=\"DROP INDEX emailIdx\"")
assert !sqlText.contains("password=${config_password}")

def con, s;
try {
    def props = new Properties();
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new com.mysql.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}/${config_dbname}", props)
    s = con.createStatement();
    r = s.executeQuery("SHOW INDEX FROM person")
    assert r.first(); // that's the primary key
    assert !r.next(); // no further rows - no more indexes
    r.close()
} finally {
    s?.close();
    con?.close();
}

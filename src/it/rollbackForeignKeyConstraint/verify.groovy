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
assert buildLogText.contains("ChangeSet test-changelog.xml::1::Alice ran successfully")
assert buildLogText.contains("ChangeSet test-changelog.xml::2::Alice ran successfully")
assert buildLogText.contains("ChangeSet test-changelog.xml::3::Alice ran successfully")
assert buildLogText.contains("ChangeSet test-changelog.xml::4::Alice ran successfully")
assert buildLogText.contains("ChangeSet test-changelog.xml::5::Alice ran successfully")

assert buildLogText.contains("Rolling Back Changeset:test-changelog.xml::5::Alice")
assert buildLogText.contains("Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP FOREIGN KEY _FK_2\"")
assert buildLogText.contains("Successfully altered `testdb`.`person`.")
assert buildLogText.contains("Rolling Back Changeset:test-changelog.xml::4::Alice")
assert buildLogText.contains("Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP FOREIGN KEY FK_1\"")
assert buildLogText.contains("Successfully altered `testdb`.`address`.")

File sql = new File( basedir, 'target/liquibase/migrate.sql' )
assert sql.exists()
def sqlText = sql.text;
assert sqlText.contains("pt-online-schema-change")
assert !sqlText.contains("password=${config_password}")
assert sqlText.contains("pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP FOREIGN KEY _FK_2\"")
// note: the foreign key is wrong here, since this is an offline execution and we didn't check for the current constraint name...
assert sqlText.contains("pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP FOREIGN KEY _FK_1\"")


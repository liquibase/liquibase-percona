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

// changeset 3: email has usePercona="false"
assert buildLogText.contains("Rolling Back Changeset: test-changelog.sql::3::Alice")
assert !buildLogText.contains("Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP COLUMN email\" --password=*** --execute h=${config_host},P=${config_port},u=${config_user},D=testdb,t=person");
// changeset 2: address
assert buildLogText.contains("Rolling Back Changeset: test-changelog.sql::2::Alice")
assert buildLogText.contains("Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP COLUMN address\" --password=*** --execute h=${config_host},P=${config_port},u=${config_user},D=testdb,t=person");
// changeset 1: table
assert buildLogText.contains("Rolling Back Changeset: test-changelog.sql::1::Alice")


File sql = new File( basedir, 'target/liquibase/migrate.sql' )
assert sql.exists()
def sqlText = sql.text;
assert sqlText.contains("pt-online-schema-change")
// changeset 3: email has usePercona="false"
assert !sqlText.contains("--  pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP COLUMN email\" --password=*** --execute h=${config_host},P=${config_port},u=${config_user},D=testdb,t=person;");
// changeset 2: address
assert sqlText.contains("--  pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"DROP COLUMN address\" --password=*** --execute h=${config_host},P=${config_port},u=${config_user},D=testdb,t=person;");
assert !sqlText.contains("password=${config_password}")

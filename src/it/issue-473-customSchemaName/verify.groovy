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

assert buildLogText.contains("Executing: pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"ADD COLUMN `test_column` DATETIME NULL DEFAULT NULL,\n" +
        " ADD INDEX `test_column_idx` (`test_column` ASC)\" --password=*** --execute h=127.0.0.1,P=${config_port},u=root,D=test_database,t=test_table\n")
assert buildLogText.contains("Created new table test_database._test_table_new OK.")
assert buildLogText.contains("ChangeSet test-changelog.sql::4::test ran successfully")
assert buildLogText.contains("Successfully altered `test_database`.`test_table`.")

File sql = new File( basedir, 'target/liquibase/migrate.sql' )
assert sql.exists()
def sqlText = sql.text
assert sqlText.contains("--  pt-online-schema-change --alter-foreign-keys-method=auto --nocheck-unique-key-change --alter=\"ADD COLUMN `test_column` DATETIME NULL DEFAULT NULL,")
assert sqlText.contains("--   ADD INDEX `test_column_idx` (`test_column` ASC)\" --password=*** --execute h=127.0.0.1,P=${config_port},u=root,D=test_database,t=test_table")
assert !sqlText.contains("password=${config_password}")

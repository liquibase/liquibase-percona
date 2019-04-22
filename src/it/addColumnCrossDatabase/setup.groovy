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

def con, s;
try {
    def props = new Properties();
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new com.mysql.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}?useSSL=false", props)
    s = con.createStatement();
    s.execute("DROP DATABASE IF EXISTS `${config_dbname}`")
    s.execute("CREATE DATABASE `${config_dbname}`")
    s.execute("DROP DATABASE IF EXISTS `${config_dbname}_cross`")
    s.execute("CREATE DATABASE `${config_dbname}_cross`")
} finally {
    s?.close();
    con?.close();
}

println "Prepared empty database `${config_dbname}`"
println "Prepared empty database `${config_dbname}_cross`"

return true

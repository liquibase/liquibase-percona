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


File setupScript = new File( prebuild_hook_script)
println "Running ${setupScript} in ${basedir} ..."

File testBasedir = basedir
if ( basedir.path.endsWith( setupScript.parent ) ) {
    testBasedir = new File( basedir.path.substring(0, basedir.path.length() - setupScript.parent.length() ) )
}
println "testBasedir: ${testBasedir}"

if ( testBasedir.name == setupScript.parentFile.name ) {
    println "Skipping setup of setupScripts"
    return true
}

def customSetup = new File( testBasedir, setupScript.name + '.groovy' )
if ( customSetup.exists() ) {
    println "Custom preBuildHookScript ${customSetup} detected, calling it instead of ${setupScript}"
    return evaluate( customSetup )
}


println "Running shared setup script ..."

def con, s;
try {
    def props = new Properties();
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new com.mysql.cj.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}?useSSL=false&allowPublicKeyRetrieval=true", props)
    s = con.createStatement();
    s.execute("DROP DATABASE IF EXISTS `${config_dbname}`")
    s.execute("CREATE DATABASE `${config_dbname}`")
} finally {
    s?.close();
    con?.close();
}

println "Prepared empty database `${config_dbname}`"

// create directories under target to silence out liquibase plugin
new File( testBasedir, "target/classes" ).mkdirs()
new File( testBasedir, "target/test-classes" ).mkdirs()

return true

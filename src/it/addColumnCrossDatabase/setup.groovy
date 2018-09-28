def con, s;
try {
    def props = new Properties();
    props.setProperty("user", config_user)
    props.setProperty("password", config_password)
    con = new com.mysql.jdbc.Driver().connect("jdbc:mysql://${config_host}:${config_port}", props)
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

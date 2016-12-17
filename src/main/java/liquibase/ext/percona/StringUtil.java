package liquibase.ext.percona;

public class StringUtil {
    private StringUtil() { }

    /**
     * Simple isNotEmpty check, avoiding NPE.
     * Note: not use StringUtil.isNotEmpty, since this method/class doesn't exist
     * for liquibase 3.3.x and 3.4.x.
     * @param s the string to test
     * @return <code>true</code> if s is not null and not empty, <code>false</code> otherwise.
     */
    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}

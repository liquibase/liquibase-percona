package liquibase.ext.percona;

class AlterTableParser {
    private final String originalSql;
    private boolean valid;
    private String targetDatabaseName;
    private String targetTableName;
    private String options;

    private int currentPosition;

    public AlterTableParser(String originalSql) {
        this.originalSql = originalSql;
        parse();
    }

    public boolean isValid() {
        return valid;
    }

    public String getTargetDatabaseName() {
        if (!isValid()) {
            throw new IllegalStateException();
        }
        return targetDatabaseName;
    }

    public String getTargetTableName() {
        if (!isValid()) {
            throw new IllegalStateException();
        }
        return targetTableName;
    }

    public String getAlterTableOptions() {
        if (!isValid()) {
            throw new IllegalStateException();
        }
        return options;
    }

    private void parse() {
        currentPosition = skipWhitespace(0);

        if (!skipKeyword("alter") || !skipKeyword("table")) {
            return;
        }

        String tableName = getNextIdentifier();
        String databaseName = null;
        if (isQualified()) {
            databaseName = tableName;
            tableName = getNextIdentifier();
        }

        valid = true;
        targetDatabaseName = databaseName;
        targetTableName = tableName;
        options = originalSql.substring(currentPosition).trim();
    }

    private int skipWhitespace(int start) {
        int newPosition = start;
        while (Character.isWhitespace(originalSql.charAt(newPosition))) {
            newPosition++;
        }
        return newPosition;
    }

    private int skipToNextWhitespace(int start) {
        int newPosition = start;
        while (!Character.isWhitespace(originalSql.charAt(newPosition))) {
            newPosition++;
        }
        return newPosition;
    }

    private boolean skipKeyword(String keyword) {
        int newPosition = skipToNextWhitespace(currentPosition);
        if (!keyword.equalsIgnoreCase(originalSql.substring(currentPosition, newPosition))) {
            return false;
        }
        currentPosition = skipWhitespace(newPosition);
        return true;
    }

    private String getNextQuotedIdentifier(char quotingChar) {
        currentPosition++;
        int next = skipToNext(currentPosition, quotingChar);
        String identifier = originalSql.substring(currentPosition, next);
        next++;
        currentPosition = skipWhitespace(next);
        return identifier;
    }

    private String getNextIdentifier() {
        char nextChar = originalSql.charAt(currentPosition);
        if (nextChar == '`' || nextChar == '"') {
            return getNextQuotedIdentifier(nextChar);
        }

        int next = skipToNextDelimiter(currentPosition);
        String identifier = originalSql.substring(currentPosition, next);
        currentPosition = skipWhitespace(next);
        return identifier;
    }

    private boolean isQualified() {
        char nextChar = originalSql.charAt(currentPosition);
        if (nextChar == '.') {
            currentPosition++;
            currentPosition = skipWhitespace(currentPosition);
            return true;
        }
        return false;
    }

    private int skipToNext(int start, char ch) {
        int newPosition = start;
        while (ch != originalSql.charAt(newPosition)) {
            newPosition++;
        }
        return newPosition;
    }

    private int skipToNextDelimiter(int start) {
        int newPosition = start;
        while (!Character.isWhitespace(originalSql.charAt(newPosition)) && originalSql.charAt(newPosition) != '.') {
            newPosition++;
        }
        return newPosition;
    }
}

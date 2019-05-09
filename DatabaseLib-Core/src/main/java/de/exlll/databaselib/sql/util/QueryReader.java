package de.exlll.databaselib.sql.util;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

final class QueryReader {
    private final Reader reader;
    private final char delimiter;

    QueryReader(Reader reader, char delimiter) {
        this.reader = reader;
        this.delimiter = delimiter;
    }

    public List<String> readQueries() throws IOException {
        List<String> queries = new ArrayList<>();

        String query;
        while (!(query = readQuery()).isEmpty()) {
            queries.add(query.trim());
        }

        return queries;
    }

    private String readQuery() throws IOException {
        StringBuilder builder = new StringBuilder();

        int value;
        while ((value = reader.read()) != -1) {
            char c = (char) value;

            if (c == '"') {
                readToClosingQuote(builder, '"');
            } else if (c == '\'') {
                readToClosingQuote(builder, '\'');
            } else if (c == delimiter) {
                return builder.toString();
            } else {
                builder.append((c == '\n') ? ' ' : c);
            }
        }

        return builder.toString();
    }

    private void readToClosingQuote(StringBuilder builder, char quoteChar)
            throws IOException {
        builder.append(quoteChar);

        int value;
        boolean escaped = false;
        while ((value = reader.read()) != -1) {
            char c = (char) value;

            if (c == quoteChar && !escaped) {
                builder.append(quoteChar);
                return;
            }

            escaped = (c == '\\' && !escaped);
            builder.append(c);
        }
    }
}

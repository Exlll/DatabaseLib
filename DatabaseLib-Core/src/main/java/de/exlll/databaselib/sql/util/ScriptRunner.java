package de.exlll.databaselib.sql.util;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * A {@code ScriptRunner} is a utility class to execute SQL scripts.
 * <p>
 * An SQL script is a file that contains SQL queries delimited by ';' (semicolon).
 * You can create a {@code ScriptRunner} by passing an instance of a {@link Reader}
 * and a {@link Connection} to its constructor. A {@code ScriptRunner} has the ability
 * to replace any part of a query prior to executing it.
 */
public final class ScriptRunner {
    private static final Logger queryLogger = Logger.getLogger(
            ScriptRunner.class.getName()
    );
    private final QueryReader queryReader;
    private final Connection connection;
    private Map<String, ?> replacements = Collections.emptyMap();
    private boolean logQueries;

    /**
     * Creates a new {@code ScriptRunner} that executes queries read from the
     * given {@code Reader} using the given {@code Connection}.
     *
     * @param reader     {@code Reader} queries are read from
     * @param connection {@code Connection} used to execute queries
     * @throws NullPointerException if any argument is null
     */
    public ScriptRunner(Reader reader, Connection connection) {
        this.queryReader = new QueryReader(
                Objects.requireNonNull(reader), ';'
        );
        this.connection = Objects.requireNonNull(connection);
    }

    /**
     * Executes all queries from the given {@code Reader}.
     *
     * @throws IOException  if an I/O error occurs while reading from the {@code Reader}
     * @throws SQLException if a database access error occurred or
     *                      if at least on of the queries failed to execute
     */
    public void runScript() throws IOException, SQLException {
        List<String> queries = queryReader.readQueries();
        try (Statement stmt = connection.createStatement()) {
            for (String query : queries) {
                executeQuery(stmt, query);
            }
        }
    }

    private void executeQuery(Statement stmt, String query) throws SQLException {
        query = preProcess(query);
        if (logQueries) {
            queryLogger.info(query);
        }
        stmt.execute(query);
    }

    private String preProcess(String query) {
        for (Map.Entry<String, ?> entry : replacements.entrySet()) {
            String key = entry.getKey();
            String replacement = (entry.getValue() == null)
                    ? "null"
                    : entry.getValue().toString();
            query = query.replace(key, replacement);
        }
        return query;
    }

    /**
     * Sets the query replacements. Defaults to an empty map.
     * <p>
     * Before a query is executed, all parts of the query that
     * match a key of the map are replaced with the value to
     * which the key is mapped.
     *
     * @param replacements the query replacements
     * @return this {@code ScriptRunner}
     * @throws NullPointerException if {@code replacements} or any of its keys is null
     */
    public ScriptRunner setReplacements(Map<String, ?> replacements) {
        for (String key : replacements.keySet()) {
            Objects.requireNonNull(key, "Map must not contain null keys.");
        }
        this.replacements = replacements;
        return this;
    }

    /**
     * Enables or disables query logging. Default value is false.
     * Each query is logged right before it is executed.
     *
     * @param logQueries true if queries should be logged, false otherwise
     * @return this {@code ScriptRunner}
     */
    public ScriptRunner setLogQueries(boolean logQueries) {
        this.logQueries = logQueries;
        return this;
    }
}

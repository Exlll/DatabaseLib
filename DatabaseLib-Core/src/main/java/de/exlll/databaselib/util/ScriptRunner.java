package de.exlll.databaselib.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ScriptRunner {
    private static final Logger logger = Logger.getLogger(ScriptRunner.class.getName());
    private final Connection connection;
    private final InputStream inputStream;
    private String delimiter = ";";
    private String charsetName = "UTF-8";
    private Map<String, ?> replacements = Collections.emptyMap();
    private boolean trimQueries = true;
    private boolean logQueries = false;
    private boolean closeConnection = true;
    private boolean closeStream = true;

    public ScriptRunner(Connection con, InputStream in) {
        Objects.requireNonNull(con);
        Objects.requireNonNull(in);
        this.connection = con;
        this.inputStream = in;
    }

    /**
     * Executes all queries from the given {@code InputStream}.
     * <p>
     * The {@code Connection} and the {@code  InputStream} are
     * by default closed after all queries have been executed.
     *
     * @throws SQLException if a database access error occurred or
     *                      if at least on of the queries failed
     */
    public void runScript() throws SQLException {
        Scanner scanner = new Scanner(inputStream, charsetName);
        scanner.useDelimiter(delimiter);

        while (scanner.hasNext()) {
            String input = preProcess(scanner.next());
            executeStatement(input);
        }
        closeResources();
    }

    private String preProcess(String input) {
        if (trimQueries) {
            input = input.trim();
        }
        for (Map.Entry<String, ?> entry : replacements.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue().toString();
            input = input.replace(key, val);
        }
        return input;
    }

    private void executeStatement(String query) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            if (logQueries) {
                logger.info("...now executing: " + query);
            }
            stmt.execute(query);
        }
    }

    private void closeResources() {
        if (closeConnection) {
            closeConnection();
        }
        if (closeStream) {
            closeInputStream();
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "exception while closing Connection", e);
        }
    }

    private void closeInputStream() {
        try {
            inputStream.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "exception while closing InputStream", e);
        }
    }

    /**
     * Sets the charset name. The default charset name is "UTF-8".
     *
     * @param charsetName name of the charset
     * @return this {@code ScriptRunner}
     * @throws IllegalCharsetNameException if {@code charsetName} is illegal (e.g. empty)
     * @throws NullPointerException        if {@code charsetName} is null
     * @throws UnsupportedCharsetException if named charset is unsupported
     */
    public ScriptRunner setCharsetName(String charsetName) {
        Objects.requireNonNull(charsetName);
        Charset.forName(charsetName); // throw exceptions early
        this.charsetName = charsetName;
        return this;
    }

    /**
     * Sets the query delimiter. The default delimiter is ";".
     *
     * @param delimiter query delimiter
     * @return this {@code ScriptRunner}
     * @throws IllegalArgumentException if {@code delimiter} is empty
     * @throws NullPointerException     if {@code delimiter} is null
     */
    public ScriptRunner setDelimiter(String delimiter) {
        Objects.requireNonNull(delimiter);
        if (delimiter.length() == 0) {
            throw new IllegalArgumentException("delimiter may not be empty");
        }
        this.delimiter = delimiter;
        return this;
    }

    /**
     * Enables or disables query trimming. Query trimming is
     * enabled by default.
     *
     * @param trim true to enable query trimming, false to disable it
     * @return this {@code ScriptRunner}
     */
    public ScriptRunner setTrimQueries(boolean trim) {
        this.trimQueries = trim;
        return this;
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
     * @throws NullPointerException if {@code replacements} is null
     */
    public ScriptRunner setReplacements(Map<String, ?> replacements) {
        Objects.requireNonNull(replacements);
        this.replacements = replacements;
        return this;
    }

    /**
     * Enables or disables automatic closing of the {@code Connection}.
     * {@code Connection}s are closed by default.
     * <p>
     * If set to true, the {@code Connection} is closed after
     * {@link #runScript()} has run, otherwise, it stays open.
     *
     * @param closeConnection true if the {@code Connection} should be
     *                        closed after usage, false otherwise
     * @return this {@code ScriptRunner}
     */
    public ScriptRunner setCloseConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
        return this;
    }

    /**
     * Enables or disables automatic closing of the {@code InputStream}.
     * {@code InputStream}s are closed by default.
     * <p>
     * If set to true, the {@code InputStream} is closed after
     * {@link #runScript()} has run, otherwise, it stays open.
     *
     * @param closeStream true if the {@code InputStream} should be
     *                    closed after usage, false otherwise
     * @return this {@code ScriptRunner}
     */
    public ScriptRunner setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
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

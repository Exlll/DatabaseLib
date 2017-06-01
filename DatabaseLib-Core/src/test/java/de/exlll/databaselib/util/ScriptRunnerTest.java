package de.exlll.databaselib.util;

import de.exlll.databaselib.DummyConnection;
import de.exlll.databaselib.ForwardingInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;
import java.nio.charset.IllegalCharsetNameException;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ScriptRunnerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private DummyConnection connection;
    private ForwardingInputStream inputStream;

    @Before
    public void setUp() throws Exception {
        connection = new DummyConnection();
        inputStream = new ForwardingInputStream(
                ClassLoader.getSystemResourceAsStream("util/test.sql")
        );
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
        inputStream.close();
    }

    @Test
    public void constructorRequiresNonNullConnection() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ScriptRunner(null, ClassLoader.getSystemResourceAsStream("util/test.sql"));
    }

    @Test
    public void constructorRequiresNonNullInputStream() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ScriptRunner(new DummyConnection(), null);
    }

    @Test
    public void runnerRequiresNonNullDelimiter() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ScriptRunner(connection, inputStream).setDelimiter(null);
    }

    @Test
    public void runnerRequiresNonEmptyDelimiter() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        new ScriptRunner(connection, inputStream).setDelimiter("");
    }

    @Test
    public void runnerRequiresNonNullCharsetName() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ScriptRunner(connection, inputStream).setCharsetName(null);
    }

    @Test
    public void runnerRequiresValidCharsetName() throws Exception {
        expectedException.expect(IllegalCharsetNameException.class);
        new ScriptRunner(connection, inputStream).setCharsetName("");
    }

    @Test
    public void runnerRequiresNonNullReplacements() throws Exception {
        expectedException.expect(NullPointerException.class);
        new ScriptRunner(connection, inputStream).setReplacements(null);
    }

    @Test
    public void runScriptRunsScript() throws Exception {
        ScriptRunner runner = new ScriptRunner(connection, inputStream);
        runner.runScript();
        assertThat(connection.executedQueries, is(getQueries(true)));
    }

    @Test
    public void queryTrimmingCanBeDisabled() throws Exception {
        ScriptRunner runner = new ScriptRunner(connection, inputStream);
        runner.runScript();
        assertThat(connection.executedQueries, is(getQueries(true)));

        resetResources();

        runner = new ScriptRunner(connection, inputStream)
                .setTrimQueries(false);
        runner.runScript();
        assertThat(connection.executedQueries, is(getQueries(false)));
    }

    @Test
    public void connectionClosingCanBeDisabled() throws Exception {
        ScriptRunner runner = new ScriptRunner(connection, inputStream);
        runner.runScript();
        assertThat(connection.isClosed(), is(true));

        resetResources();

        runner = new ScriptRunner(connection, inputStream)
                .setCloseConnection(false);
        runner.runScript();
        assertThat(connection.isClosed(), is(false));
    }

    @Test
    public void inputStreamClosingCanBeDisabled() throws Exception {
        ScriptRunner runner = new ScriptRunner(connection, inputStream);
        runner.runScript();
        assertThat(inputStream.closed, is(true));

        resetResources();

        runner = new ScriptRunner(connection, inputStream)
                .setCloseStream(false);
        runner.runScript();
        assertThat(inputStream.closed, is(false));
    }

    @Test
    public void replacementsAreUsed() throws Exception {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("test", "test_table");
        replacements.put("id", "user_id");

        ScriptRunner runner = new ScriptRunner(connection, inputStream)
                .setReplacements(replacements);
        runner.runScript();

        assertThat(connection.executedQueries, is(getQueries("util/test.replaced.sql")));
    }

    private List<String> getQueries(boolean trim) {
        return getQueries("util/test.sql", trim);
    }

    private List<String> getQueries(String file) {
        return getQueries(file, true);
    }

    private List<String> getQueries(String file, boolean trim) {
        List<String> queries = new ArrayList<>();

        InputStream in = ClassLoader.getSystemResourceAsStream(file);
        try (Scanner scanner = new Scanner(in).useDelimiter(";")) {
            while (scanner.hasNext()) {
                String input = scanner.next();
                queries.add(trim ? input.trim() : input);
            }
        }
        return queries;
    }

    private void resetResources() throws Exception {
        connection.close();
        inputStream.close();
        connection = new DummyConnection();
        inputStream = new ForwardingInputStream(
                ClassLoader.getSystemResourceAsStream("util/test.sql")
        );
    }
}
package de.exlll.databaselib.sql.util;

import de.exlll.databaselib.sql.DummyConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptRunnerTest {
    private ScriptRunner runner;
    private DummyConnection connection;
    private Reader reader;

    @BeforeEach
    void setUp() {
        connection = new DummyConnection();
        reader = new StringReader(QueryReaderTest.SQL_INPUT);
        runner = new ScriptRunner(reader, connection);
    }

    @Test
    void constructorRequiresNonNullArgs() {
        assertThrows(NullPointerException.class,
                () -> new ScriptRunner(null, connection));
        assertThrows(NullPointerException.class,
                () -> new ScriptRunner(reader, null));
    }

    @Test
    void setReplacementsRequiresNonNullKeys() {
        assertThrows(NullPointerException.class,
                () -> runner.setReplacements(null)
        );
        String msg = assertThrows(NullPointerException.class,
                () -> runner.setReplacements(mapOf(null, ""))
        ).getMessage();
        assertThat(msg, is("Map must not contain null keys."));

        runner.setReplacements(mapOf("", ""));
    }

    @Test
    void runScriptExecutesQueries() throws Exception {
        runner.runScript();
        List<String> executedQueries = connection.getLastStatement()
                .getExecutedQueries();
        assertThat(executedQueries, is(QueryReaderTest.QUERIES));
    }

    @Test
    void runScriptAppliesReplacements() throws Exception {
        final String query = "SELECT %X% FROM %Y% WHERE name = '%X%'";
        testReplacements(
                query, mapOf("%X%", "TEST"),
                "SELECT TEST FROM %Y% WHERE name = 'TEST'"
        );
        testReplacements(
                query, mapOf("%X%", "TEST1", "%Y%", "TEST2"),
                "SELECT TEST1 FROM TEST2 WHERE name = 'TEST1'"
        );
    }

    private void testReplacements(
            String query, Map<String, ?> replacements, String expectedResult
    ) throws Exception {
        DummyConnection connection = new DummyConnection();
        Reader reader = new StringReader(query);
        runner = new ScriptRunner(reader, connection)
                .setReplacements(replacements);
        runner.runScript();
        List<String> executedQueries = connection.getLastStatement()
                .getExecutedQueries();
        List<String> expected = Collections.singletonList(expectedResult);
        assertThat(executedQueries, is(expected));
    }

    private static <K, T> Map<K, T> mapOf(K k1, T v1) {
        Map<K, T> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }

    private static <K, T> Map<K, T> mapOf(K k1, T v1, K k2, T v2) {
        Map<K, T> map = mapOf(k1, v1);
        map.put(k2, v2);
        return map;
    }
}
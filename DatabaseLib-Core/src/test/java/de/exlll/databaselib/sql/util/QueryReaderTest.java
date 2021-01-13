package de.exlll.databaselib.sql.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class QueryReaderTest {
    public static final String SQL_INPUT = "CREATE TABLE IF NOT EXISTS `test`\n" +
            "(\n" +
            "    `id`   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            "    `name` VARCHAR(32)\n" +
            ") DEFAULT CHARACTER SET utf8;\n" +
            "\n" +
            "SELECT * FROM `test` WHERE `id` = 10;\n" +
            "SELECT * FROM `test` WHERE `name` = \";\";\n" +
            "SELECT * FROM `test` WHERE `name` = \"\\\"\";\n" +
            "SELECT * FROM `test` WHERE `name` = \"\\\\\";\n" +
            "SELECT * FROM `test` WHERE `name` = \"\\\\\\\"\";\n" +
            "SELECT * FROM `test` WHERE `name` = ';';\n" +
            "SELECT * FROM `test` WHERE `name` = '\\'';\n" +
            "SELECT * FROM `test` WHERE `name` = '\\\\';\n" +
            "SELECT * FROM `test` WHERE `name` = '\\\\\\'';";
    private static final String SQL_INPUT_PIPE_DELIM = "CREATE TABLE IF NOT EXISTS `test`\n" +
            "(\n" +
            "    `id`   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            "    `name` VARCHAR(32)\n" +
            ") DEFAULT CHARACTER SET utf8|\n" +
            "\n" +
            "SELECT * FROM `test` WHERE `id` = 10|\n" +
            "SELECT * FROM `test` WHERE `name` = \"|\"|\n" +
            "SELECT * FROM `test` WHERE `name` = \"\\\"\"|\n" +
            "SELECT * FROM `test` WHERE `name` = \"\\\\\"|\n" +
            "SELECT * FROM `test` WHERE `name` = \"\\\\\\\"\"|\n" +
            "SELECT * FROM `test` WHERE `name` = '|'|\n" +
            "SELECT * FROM `test` WHERE `name` = '\\''|\n" +
            "SELECT * FROM `test` WHERE `name` = '\\\\'|\n" +
            "SELECT * FROM `test` WHERE `name` = '\\\\\\''|";
    public static final List<String> QUERIES = Arrays.asList(
            "CREATE TABLE IF NOT EXISTS `test` ( " +
                    "    `id`   INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "    `name` VARCHAR(32) ) " +
                    "DEFAULT CHARACTER SET utf8",
            "SELECT * FROM `test` WHERE `id` = 10",
            "SELECT * FROM `test` WHERE `name` = \";\"",
            "SELECT * FROM `test` WHERE `name` = \"\\\"\"",
            "SELECT * FROM `test` WHERE `name` = \"\\\\\"",
            "SELECT * FROM `test` WHERE `name` = \"\\\\\\\"\"",
            "SELECT * FROM `test` WHERE `name` = ';'",
            "SELECT * FROM `test` WHERE `name` = '\\''",
            "SELECT * FROM `test` WHERE `name` = '\\\\'",
            "SELECT * FROM `test` WHERE `name` = '\\\\\\''"
    );


    @Test
    void readQueriesReadsAllQueries() throws IOException {
        QueryReader reader = new QueryReader(
                new StringReader(SQL_INPUT), ';'
        );
        assertThat(reader.readQueries(), is(QUERIES));
    }

    @Test
    void readQueriesUsesDelimiter() throws IOException {
        QueryReader reader = new QueryReader(
                new StringReader(SQL_INPUT_PIPE_DELIM), '|'
        );
        List<String> queries = new ArrayList<>(QueryReaderTest.QUERIES);
        queries.replaceAll(s -> s.replace(';', '|'));
        assertThat(reader.readQueries(), is(queries));
    }
}
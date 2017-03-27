package de.exlll.databaselib.submit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlPreparedStatementTaskTest {
    @Test
    public void executeAppliesPreparedStatement() throws Exception {
        SqlPreparedStatementTask<?> task = new SqlPreparedStatementTask<>(
                "", statement -> SqlTaskTest.HELLO_WORLD, null
        );
        task.execute(new DummyConnection());
        assertThat(task.result, is(SqlTaskTest.HELLO_WORLD));
        assertThat(task.exception, nullValue());
    }

    @Test
    public void executeSetsTimeoutBeforeApplyingFunction() throws Exception {
        SqlPreparedStatementTask<?> task = new SqlPreparedStatementTask<>(
                "",
                statement -> {
                    assertThat(statement.getQueryTimeout(), is(SqlTask.DEFAULT_QUERY_TIMEOUT));
                    return null;
                }, null
        );

        task.execute(new DummyConnection());

        if (task.exception != null) {
            throw task.exception;
        }
    }

    @Test
    public void executeSetsExceptionOnException() throws Exception {
        SqlPreparedStatementTask<?> task = new SqlPreparedStatementTask<>(
                "", null, null
        );

        task.execute(null);

        assertThat(task.exception, instanceOf(NullPointerException.class));
    }
}
package de.exlll.databaselib.submit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class SqlStatementTaskTest {
    @Test
    public void executeAppliesStatement() throws Exception {
        SqlStatementTask<?> task = new SqlStatementTask<>(
                statement -> SqlTaskTest.HELLO_WORLD, null
        );
        task.execute(new DummyConnection());
        assertThat(task.result, is(SqlTaskTest.HELLO_WORLD));
        assertThat(task.exception, nullValue());
    }

    @Test
    public void executeSetsTimeoutBeforeApplyingFunction() throws Exception {
        SqlStatementTask<?> task = new SqlStatementTask<>(
                statement -> {
                    assertThat(statement.getQueryTimeout(), is(5));
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
        SqlStatementTask<?> task = new SqlStatementTask<>(null, null);
        task.execute(null);

        assertThat(task.exception, instanceOf(NullPointerException.class));
    }
}
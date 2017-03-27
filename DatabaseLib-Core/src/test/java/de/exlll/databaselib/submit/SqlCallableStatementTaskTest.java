package de.exlll.databaselib.submit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlCallableStatementTaskTest {
    @Test
    public void executeAppliesCallableStatement() throws Exception {
        SqlCallableStatementTask<?> task = new SqlCallableStatementTask<>(
                "", statement -> SqlTaskTest.HELLO_WORLD, null
        );
        task.execute(new DummyConnection());
        assertThat(task.result, is(SqlTaskTest.HELLO_WORLD));
        assertThat(task.exception, nullValue());
    }

    @Test
    public void executeSetsTimeoutBeforeApplyingFunction() throws Exception {
        SqlCallableStatementTask<?> task = new SqlCallableStatementTask<>(
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
        SqlCallableStatementTask<?> task = new SqlCallableStatementTask<>(
                "", null, null
        );
        task.execute(null);

        assertThat(task.exception, instanceOf(NullPointerException.class));
    }
}
package de.exlll.databaselib.submit;

import de.exlll.databaselib.DummyConnection;
import de.exlll.databaselib.submit.configure.PreparationStrategy;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.*;
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
        SqlCallableStatementTask<?> task = new SqlCallableStatementTask<>(
                "", null, null
        );
        task.execute(null);

        assertThat(task.exception, instanceOf(NullPointerException.class));
    }

    @Test
    public void executeUsesPreparationStrategy() throws Exception {
        DummyConnection connection = new DummyConnection();
        AtomicInteger calls = connection.prepareCallCalls;

        assertThat(calls.get(), is(0));
        SqlCallableStatementTask<?> task = new SqlCallableStatementTask<>("", null, null);

        task.execute(connection);
        assertThat(calls.get(), is(1));

        task.setPreparationStrategy(
                (connection1, query) -> connection.prepareCall("", 1, 1)
        ).execute(connection);
        assertThat(calls.get(), is(3));

        task.setPreparationStrategy(
                (connection1, query) -> connection.prepareCall("", 1, 1, 1)
        ).execute(connection);
        assertThat(calls.get(), is(7));

        task.setPreparationStrategy(PreparationStrategy.CALLABLE_STATEMENT_DEFAULT)
                .execute(connection);
        assertThat(calls.get(), is(8));
    }
}
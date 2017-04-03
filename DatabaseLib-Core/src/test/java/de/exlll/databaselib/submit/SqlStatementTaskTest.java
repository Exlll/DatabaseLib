package de.exlll.databaselib.submit;

import de.exlll.databaselib.submit.configure.PreparationStrategy;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

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
        SqlStatementTask<?> task = new SqlStatementTask<>(null, null);
        task.execute(null);

        assertThat(task.exception, instanceOf(NullPointerException.class));
    }

    @Test
    public void executeUsesPreparationStrategy() throws Exception {
        DummyConnection connection = new DummyConnection();
        AtomicInteger calls = connection.prepareStatementCalls;

        assertThat(calls.get(), is(0));
        SqlPreparedStatementTask<?> task = new SqlPreparedStatementTask<>("", null, null);

        task.execute(connection);
        assertThat(calls.get(), is(1));

        task.setPreparationStrategy(PreparationStrategy.withAutoGeneratedKeys(-1))
                .execute(connection);
        assertThat(calls.get(), is(3));

        task.setPreparationStrategy(PreparationStrategy.withColumnIndexes(1))
                .execute(connection);
        assertThat(calls.get(), is(7));

        task.setPreparationStrategy(PreparationStrategy.withColumnNames(""))
                .execute(connection);
        assertThat(calls.get(), is(15));

        task.setPreparationStrategy(PreparationStrategy.DEFAULT)
                .execute(connection);
        assertThat(calls.get(), is(16));

    }
}
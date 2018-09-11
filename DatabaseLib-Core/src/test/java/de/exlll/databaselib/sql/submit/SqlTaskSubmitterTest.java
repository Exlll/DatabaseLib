package de.exlll.databaselib.sql.submit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlTaskSubmitterTest {
    private TestSubmitter submitter;

    @BeforeEach
    void setUp() {
        submitter = new TestSubmitter();
    }

    @Test
    void toBiConsumerRequiresNonNullConsumer() {
        assertThrows(
                NullPointerException.class,
                () -> SqlTaskSubmitter.toBiConsumer(null)
        );
    }

    @Test
    void toBiConsumerWrapsConsumer() {
        AtomicInteger integer = new AtomicInteger();
        Consumer<Throwable> consumer = t -> {
            integer.incrementAndGet();
            assertThat(t.getMessage(), is("HI"));
        };
        BiConsumer<?, ? super Throwable> biConsumer =
                SqlTaskSubmitter.toBiConsumer(consumer);
        biConsumer.accept(null, new RuntimeException("HI"));
        assertThat(integer.get(), is(1));
    }

    @Test
    void submitSqlConnectionTaskSubmitsTask() {
        submitter.submitSqlConnectionTask(
                connection -> "", (s, throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitSqlConnectionTaskSubmitsTask2() {
        submitter.submitSqlConnectionTask(
                connection -> {}, (throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitSqlStatementTaskSubmitsTask2() {
        submitter.submitSqlStatementTask(
                statement -> {}, (throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitSqlStatementTaskSubmitsTask() {
        submitter.submitSqlStatementTask(
                statement -> "", (s, throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitSqlPreparedStatementTaskSubmitsTask2() {
        submitter.submitSqlPreparedStatementTask(
                "", preparedStatement -> {}, (throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitSqlPreparedStatementTaskSubmitsTask() {
        submitter.submitSqlPreparedStatementTask(
                "", preparedStatement -> "", (s, throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitSqlCallableStatementTaskSubmitsTask2() {
        submitter.submitSqlCallableStatementTask(
                "", callableStatement -> {}, (throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitSqlCallableStatementTaskSubmitsTask() {
        submitter.submitSqlCallableStatementTask(
                "", callableStatement -> "", (s, throwable) -> {}
        );
        assertGotIncremented();
    }

    @Test
    void submitMethodsWrapCallback() {
        TestSubmitter submitter = new TestSubmitter();
        submitter.submitSqlConnectionTask(c -> {}, t -> {});
        submitter.submitSqlConnectionTask(c -> null, (o, t) -> {});
        submitter.submitSqlStatementTask(c -> {}, t -> {});
        submitter.submitSqlStatementTask(c -> null, (o, t) -> {});
        submitter.submitSqlPreparedStatementTask("", c -> {}, t -> {});
        submitter.submitSqlPreparedStatementTask("", c -> null, (o, t) -> {});
        submitter.submitSqlCallableStatementTask("", c -> {}, t -> {});
        submitter.submitSqlCallableStatementTask("", c -> null, (o, t) -> {});
        assertThat(submitter.integer.get(), is(16));
    }

    private void assertGotIncremented() {
        assertThat(submitter.gotIncremented(), is(true));
    }

    private static final class TestSubmitter extends SqlTaskSubmitter {
        private final AtomicInteger integer = new AtomicInteger();

        boolean gotIncremented() {
            return integer.get() == 2;
        }

        @Override
        protected void submit(SqlTask<?, ?> task) {
            assertThat(task, is(notNullValue()));
            integer.incrementAndGet();
        }

        @Override
        protected Connection getConnection() {
            return null;
        }

        @Override
        <R> BiConsumer<? super R, ? super Throwable> wrapCallback(
                BiConsumer<? super R, ? super Throwable> callback
        ) {
            integer.incrementAndGet();
            return super.wrapCallback(callback);
        }
    }
}
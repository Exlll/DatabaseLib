package de.exlll.databaselib.sql.submit;

import de.exlll.databaselib.sql.DummyConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SqlTaskSubmitterTest {
    private TestSubmitter submitter;
    private AtomicInteger callCounter;

    @BeforeEach
    void setUp() {
        submitter = new TestSubmitter();
        callCounter = new AtomicInteger();
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
        submitter.assertGotIncremented();
    }

    @Test
    void wrapCompletableFutureRequiresNonNullArgument() {
        assertThrows(
                NullPointerException.class,
                () -> submitter.wrapCompletableFuture(null)
        );
    }

    @Test
    void wrappedCompletableFutureCompletes() throws Exception {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        BiConsumer<? super Integer, ? super Throwable> biConsumer =
                submitter.wrapCompletableFuture(cf);
        biConsumer.accept(1, null);
        assertThat(cf.isDone() && !cf.isCancelled() && !cf.isCompletedExceptionally(), is(true));
        assertThat(cf.get(), is(1));
    }

    @Test
    void wrappedCompletableFutureCompletesExceptionally() {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        BiConsumer<? super Integer, ? super Throwable> biConsumer =
                submitter.wrapCompletableFuture(cf);
        biConsumer.accept(1, new Exception());
        assertThat(cf.isDone() && !cf.isCancelled() && cf.isCompletedExceptionally(), is(true));
        assertThrows(ExecutionException.class, cf::get);
    }

    @Test
    void wrappedCompletableFutureIsWrappedByCallbackWrapper() {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        BiConsumer<? super Integer, ? super Throwable> biConsumer =
                submitter.wrapCompletableFuture(cf);
        biConsumer.accept(null, null);
        assertThat(submitter.getInteger(), is(1));
    }

    @Test
    void submitSqlConnectionTaskCompletesCompletableFuture() {
        addStages(submitter.submitSqlConnectionTask(connection -> callCounter.addAndGet(1)));
        assertThat(callCounter.get(), is(3));
    }

    @Test
    void submitSqlConnectionTaskCompletesCompletableFutureExceptionally() {
        addStages(submitter.submitSqlConnectionTask(connection -> {
            throw new RuntimeException();
        }));
        assertThat(callCounter.get(), is(4));
    }

    @Test
    void submitSqlStatementTaskCompletesCompletableFuture() {
        addStages(submitter.submitSqlStatementTask(statement -> callCounter.addAndGet(1)));
        assertThat(callCounter.get(), is(3));
    }

    @Test
    void submitSqlStatementTaskCompletesCompletableFutureExceptionally() {
        addStages(submitter.submitSqlStatementTask(statement -> {
            throw new RuntimeException();
        }));
        assertThat(callCounter.get(), is(4));
    }

    @Test
    void submitSqlPreparedStatementTaskCompletesCompletableFuture() {
        addStages(submitter.submitSqlPreparedStatementTask("", statement -> callCounter.addAndGet(1)));
        assertThat(callCounter.get(), is(3));
    }

    @Test
    void submitSqlPreparedStatementTaskCompletesCompletableFutureExceptionally() {
        addStages(submitter.submitSqlPreparedStatementTask("", statement -> {
            throw new RuntimeException();
        }));
        assertThat(callCounter.get(), is(4));
    }

    @Test
    void submitSqlCallableStatementTaskCompletesCompletableFuture() {
        addStages(submitter.submitSqlCallableStatementTask("", statement -> callCounter.addAndGet(1)));
        assertThat(callCounter.get(), is(3));
    }

    @Test
    void submitSqlCallableStatementTaskCompletesCompletableFutureExceptionally() {
        addStages(submitter.submitSqlCallableStatementTask("", statement -> {
            throw new RuntimeException();
        }));
        assertThat(callCounter.get(), is(4));
    }

    private void addStages(CompletionStage<?> completableFuture) {
        completableFuture.thenApply(i -> callCounter.addAndGet(2))
                .exceptionally(throwable -> callCounter.addAndGet(4));
    }

    @Test
    void applyConnectionRequiresNonNullFunction() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> submitter.applyConnection(null)
        );
        assertThat(exception.getMessage(), is("The function must not be null."));
    }

    @Test
    void applyConnectionCallsGetConnection() {
        submitter.applyConnection(connection -> null);
        assertThat(submitter.getInteger(), is(20));
    }

    @Test
    void applyConnectionAppliesConnection() {
        Connection connection = submitter.applyConnection(c -> c);
        assertThat(connection, sameInstance(submitter.connection));
    }

    @Test
    void applyConnectionRethrowsSqlExceptionsAsRuntimeExceptions() {
        SQLException test = new SQLException();
        CheckedSqlFunction<Connection, ?> function = connection -> { throw test; };
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> submitter.applyConnection(function)
        );
        assertThat(exception.getCause(), sameInstance(test));
    }

    @Test
    void applyStatementRequiresNonNullFunction() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> submitter.applyStatement(null)
        );
        assertThat(exception.getMessage(), is("The function must not be null."));
    }

    @Test
    void applyStatementCallsGetConnection() {
        submitter.applyStatement(statement -> null);
        assertThat(submitter.getInteger(), is(20));
    }

    @Test
    void applyStatementAppliesStatement() {
        Statement statement = submitter.applyStatement(s -> s);
        assertThat(statement, sameInstance(submitter.connection.getLastStatement()));
    }

    @Test
    void applyStatementRethrowsSqlExceptionsAsRuntimeExceptions() {
        SQLException test = new SQLException();
        CheckedSqlFunction<Statement, ?> function = statement -> { throw test; };
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> submitter.applyStatement(function)
        );
        assertThat(exception.getCause(), sameInstance(test));
    }

    @Test
    void applyPreparedStatementRequiresNonNullArguments() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> submitter.applyPreparedStatement(null, s -> null)
        );
        assertThat(exception.getMessage(), is("The query must not be null."));

        exception = assertThrows(
                NullPointerException.class,
                () -> submitter.applyPreparedStatement("", null)
        );
        assertThat(exception.getMessage(), is("The function must not be null."));
    }

    @Test
    void applyPreparedStatementCallsGetConnection() {
        submitter.applyPreparedStatement("", statement -> null);
        assertThat(submitter.getInteger(), is(20));
    }

    @Test
    void applyPreparedStatementAppliesStatement() {
        submitter.applyPreparedStatement("SELECT 1", s -> s);
        verify(submitter.connection).prepareStatement("SELECT 1");
    }

    @Test
    void applyPreparedStatementRethrowsSqlExceptionsAsRuntimeExceptions() {
        SQLException test = new SQLException();
        CheckedSqlFunction<PreparedStatement, ?> function = statement -> { throw test; };
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> submitter.applyPreparedStatement("", function)
        );
        assertThat(exception.getCause(), sameInstance(test));
    }

    @Test
    void applyCallableStatementRequiresNonNullArguments() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> submitter.applyCallableStatement(null, s -> null)
        );
        assertThat(exception.getMessage(), is("The query must not be null."));

        exception = assertThrows(
                NullPointerException.class,
                () -> submitter.applyCallableStatement("", null)
        );
        assertThat(exception.getMessage(), is("The function must not be null."));
    }

    @Test
    void applyCallableStatementCallsGetConnection() {
        submitter.applyCallableStatement("", statement -> null);
        assertThat(submitter.getInteger(), is(20));
    }

    @Test
    void applyCallableStatementAppliesStatement() {
        submitter.applyCallableStatement("SELECT 1", s -> s);
        verify(submitter.connection).prepareCall("SELECT 1");
    }

    @Test
    void applyCallableStatementRethrowsSqlExceptionsAsRuntimeExceptions() {
        SQLException test = new SQLException();
        CheckedSqlFunction<CallableStatement, ?> function = statement -> { throw test; };
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> submitter.applyCallableStatement("", function)
        );
        assertThat(exception.getCause(), sameInstance(test));
    }

    private static final class TestSubmitter extends SqlTaskSubmitter {
        private final AtomicInteger integer = new AtomicInteger();
        private final DummyConnection connection = spy(new DummyConnection());

        public int getInteger() {
            return integer.get();
        }

        void assertGotIncremented() {
            assertThat(integer.get(), is(2));
        }

        @Override
        protected void submit(SqlTask<?, ?> task) {
            assertThat(task, is(notNullValue()));
            integer.incrementAndGet();
            task.execute(connection);
        }

        @Override
        protected Connection getConnection() {
            integer.addAndGet(20);
            return connection;
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
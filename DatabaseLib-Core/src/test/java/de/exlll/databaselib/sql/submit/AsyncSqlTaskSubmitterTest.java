package de.exlll.databaselib.sql.submit;

import de.exlll.databaselib.sql.DummySqlConnectionPool;
import de.exlll.databaselib.sql.pool.SqlConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class AsyncSqlTaskSubmitterTest {
    private DummySqlConnectionPool dummySqlConnectionPool;

    @BeforeEach
    void setUp() {
        dummySqlConnectionPool = new DummySqlConnectionPool();
    }

    @Test
    void wrapCallbackWrapsCallbackWithSyncExecutor() {
        AtomicInteger integer = new AtomicInteger();
        AsyncSqlTaskSubmitter submitter = newSubmitter(
                c -> integer.set(20), Runnable::run
        );
        SqlStatementTask<Void> task = new SqlStatementTask<>(
                statement -> null, (aVoid, throwable) -> {}
        );
        BiConsumer<? super Void, ? super Throwable> wrapped =
                submitter.wrapCallback(task.callback);
        wrapped.accept(null, null);
        assertThat(integer.get(), is(20));
    }

    @Test
    void submitGetsConnectionFromPool() {
        AsyncSqlTaskSubmitter submitter = newSubmitter();
        submitter.submitSqlConnectionTask(c -> null, (v, t) -> {});
        assertThat(dummySqlConnectionPool.getConnectionCallCount(), is(1));
        submitter.submitSqlStatementTask(c -> null, (v, t) -> {});
        assertThat(dummySqlConnectionPool.getConnectionCallCount(), is(2));
    }

    @Test
    void submitCatchesThrowableAndExecutesTaskCallbackWithSyncCallback() {
        AtomicInteger integer = new AtomicInteger();
        AsyncSqlTaskSubmitter submitterA = newSubmitter(
                () -> {throw new RuntimeException("a");},
                c -> {
                    c.run();
                    integer.addAndGet(1);
                }
        );
        AsyncSqlTaskSubmitter submitterB = newSubmitter(
                () -> {throw new SQLException("b");},
                c -> {
                    c.run();
                    integer.addAndGet(2);
                }
        );
        AsyncSqlTaskSubmitter submitterC = newSubmitter(
                () -> {throw new Error("c");},
                c -> {
                    c.run();
                    integer.addAndGet(4);
                }
        );
        submitterA.submitSqlStatementTask(
                statement -> null, (o, t) -> {
                    assertThat(t, is(notNullValue()));
                    integer.addAndGet(8);
                }
        );
        submitterB.submitSqlConnectionTask(
                statement -> null, (o, t) -> {
                    assertThat(t, is(notNullValue()));
                    integer.addAndGet(16);
                }
        );
        submitterC.submitSqlPreparedStatementTask(
                "", statement -> {}, t -> {
                    assertThat(t, is(notNullValue()));
                    integer.addAndGet(32);
                }
        );
        assertThat(integer.get(), is((2 << 5) - 1));
    }

    @Test
    void submitExecutesTaskAsync() {
        AtomicInteger integer = new AtomicInteger();
        AsyncSqlTaskSubmitter submitter = new TestAsyncSqlTaskSubmitter(
                new DummySqlConnectionPool(), Runnable::run, command -> {
            command.run();
            integer.addAndGet(1);
        });
        submitter.submitSqlStatementTask(
                statement -> integer.addAndGet(2),
                (i, t) -> {}
        );
        assertThat(integer.get(), is(3));
    }

    @Test
    void submitExecutesCallbackSync() {
        AtomicInteger integer = new AtomicInteger();
        AsyncSqlTaskSubmitter submitter = new TestAsyncSqlTaskSubmitter(
                new DummySqlConnectionPool(), command -> {
            command.run();
            integer.addAndGet(1);
        }, Runnable::run);
        submitter.submitSqlStatementTask(
                s -> {},
                t -> integer.addAndGet(2)
        );
        assertThat(integer.get(), is(3));
    }


    private AsyncSqlTaskSubmitter newSubmitter(SqlConnectionPool pool, Executor sync) {
        return new TestAsyncSqlTaskSubmitter(pool, sync, Runnable::run);
    }

    private AsyncSqlTaskSubmitter newSubmitter() {
        return new TestAsyncSqlTaskSubmitter(
                dummySqlConnectionPool, Runnable::run, Runnable::run
        );
    }

    private AsyncSqlTaskSubmitter newSubmitter(
            Executor syncExecutor, Executor asyncExecutor
    ) {
        return new TestAsyncSqlTaskSubmitter(
                dummySqlConnectionPool, syncExecutor, asyncExecutor
        );
    }

    private static final class TestAsyncSqlTaskSubmitter extends AsyncSqlTaskSubmitter {
        TestAsyncSqlTaskSubmitter(
                SqlConnectionPool connectionPool,
                Executor syncExecutor,
                Executor asyncExecutor
        ) {
            super(connectionPool, syncExecutor, asyncExecutor);
        }
    }
}
package de.exlll.databaselib.sql.submit;

import de.exlll.databaselib.sql.pool.SqlConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

abstract class AsyncSqlTaskSubmitter extends SqlTaskSubmitter {
    private final SqlConnectionPool connectionPool;
    private final Executor syncExecutor;
    private final Executor asyncExecutor;

    AsyncSqlTaskSubmitter(
            SqlConnectionPool connectionPool,
            Executor syncExecutor,
            Executor asyncExecutor
    ) {
        this.connectionPool = connectionPool;
        this.syncExecutor = syncExecutor;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public final Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    @Override
    final void submit(SqlTask<?, ?> task) {
        asyncExecutor.execute(() -> {
            try (Connection connection = connectionPool.getConnection()) {
                task.execute(connection);
            } catch (Throwable throwable) {
                task.callback.accept(null, throwable);
            }
        });
    }

    @Override
    final <R> BiConsumer<? super R, ? super Throwable> wrapCallback(
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        Objects.requireNonNull(callback);
        return (r, t) -> syncExecutor.execute(() -> callback.accept(r, t));
    }
}

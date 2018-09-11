package de.exlll.databaselib.sql.submit;

import java.sql.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract class SqlTaskSubmitter {
    /**
     * Submits a {@code Connection} task that returns a result.
     *
     * @param function function that defines operations on a {@code Connection}
     *                 and returns a result
     * @param callback callback that accepts the result and informs about
     *                 exceptions. If no exception occurred, the accepted
     *                 {@code Throwable} is null.
     * @param <R>      the result type
     * @throws NullPointerException if any argument is null
     */
    protected final <R> void submitSqlConnectionTask(
            CheckedSqlFunction<? super Connection, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        submit(new SqlConnectionTask<>(function, wrapCallback(callback)));
    }

    /**
     * Submits a {@code Connection} task that doesn't return a result.
     *
     * @param action   action that defines operations on a {@code Connection}
     * @param callback callback that informs about exceptions. If no exception
     *                 occurred, the accepted {@code Throwable} is null.
     * @throws NullPointerException if any argument is null
     */
    protected final void submitSqlConnectionTask(
            CheckedSqlConsumer<? super Connection> action,
            Consumer<? super Throwable> callback
    ) {
        submit(new SqlConnectionTask<>(
                CheckedSqlFunction.from(action),
                wrapCallback(toBiConsumer(callback))
        ));
    }

    /**
     * Submits a {@code Statement} task that returns a result.
     *
     * @param function function that defines operations on a {@code Statement}
     *                 and returns a result
     * @param callback callback that accepts the result and informs about
     *                 exceptions. If no exception occurred, the accepted
     *                 {@code Throwable} is null.
     * @param <R>      the result type
     * @throws NullPointerException if any argument is null
     */
    protected final <R> void submitSqlStatementTask(
            CheckedSqlFunction<? super Statement, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        submit(new SqlStatementTask<>(function, wrapCallback(callback)));
    }

    /**
     * Submits a {@code Statement} task that doesn't return a result.
     *
     * @param action   action that defines operations on a {@code Statement}
     * @param callback callback that informs about exceptions. If no exception
     *                 occurred, the accepted {@code Throwable} is null.
     * @throws NullPointerException if any argument is null
     */
    protected final void submitSqlStatementTask(
            CheckedSqlConsumer<? super Statement> action,
            Consumer<? super Throwable> callback
    ) {
        submit(new SqlStatementTask<>(
                CheckedSqlFunction.from(action),
                wrapCallback(toBiConsumer(callback))
        ));
    }

    /**
     * Submits a {@code PreparedStatement} task that returns a result.
     *
     * @param function function that defines operations on a {@code PreparedStatement}
     *                 and returns a result
     * @param callback callback that accepts the result and informs about
     *                 exceptions. If no exception occurred, the accepted
     *                 {@code Throwable} is null.
     * @param <R>      the result type
     * @throws NullPointerException if any argument is null
     */
    protected final <R> void submitSqlPreparedStatementTask(
            String query,
            CheckedSqlFunction<? super PreparedStatement, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        submit(new SqlPreparedStatementTask<>(query, function, wrapCallback(callback)));
    }

    /**
     * Submits a {@code PreparedStatement} task that doesn't return a result.
     *
     * @param action   action that defines operations on a {@code PreparedStatement}
     * @param callback callback that informs about exceptions. If no exception
     *                 occurred, the accepted {@code Throwable} is null.
     * @throws NullPointerException if any argument is null
     */
    protected final void submitSqlPreparedStatementTask(
            String query,
            CheckedSqlConsumer<? super PreparedStatement> action,
            Consumer<? super Throwable> callback
    ) {
        submit(new SqlPreparedStatementTask<>(query,
                CheckedSqlFunction.from(action),
                wrapCallback(toBiConsumer(callback))
        ));
    }

    /**
     * Submits a {@code CallableStatement} task that returns a result.
     *
     * @param function function that defines operations on a {@code CallableStatement}
     *                 and returns a result
     * @param callback callback that accepts the result and informs about
     *                 exceptions. If no exception occurred, the accepted
     *                 {@code Throwable} is null.
     * @param <R>      the result type
     * @throws NullPointerException if any argument is null
     */
    protected final <R> void submitSqlCallableStatementTask(
            String query,
            CheckedSqlFunction<? super CallableStatement, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        submit(new SqlCallableStatementTask<>(query, function, wrapCallback(callback)));
    }

    /**
     * Submits a {@code CallableStatement} task that doesn't return a result.
     *
     * @param action   action that defines operations on a {@code CallableStatement}
     * @param callback callback that informs about exceptions. If no exception
     *                 occurred, the accepted {@code Throwable} is null.
     * @throws NullPointerException if any argument is null
     */
    protected final void submitSqlCallableStatementTask(
            String query,
            CheckedSqlConsumer<? super CallableStatement> action,
            Consumer<? super Throwable> callback
    ) {
        submit(new SqlCallableStatementTask<>(query,
                CheckedSqlFunction.from(action),
                wrapCallback(toBiConsumer(callback))
        ));
    }

    /**
     * Returns a {@code Connection} directly from the pool.
     * <p>
     * The connection must manually be closed after usage.
     * Be aware that this call is blocking if no {@code Connection} is
     * currently available.
     *
     * @return {@code Connection} from the pool
     * @throws SQLException if a database access error occurs
     */
    protected abstract Connection getConnection() throws SQLException;

    /**
     * Submits an {@code SqlTask} for execution.
     * <p>
     * The task is executed in whichever thread the library chooses.
     *
     * @param task task that is submitted
     */
    abstract void submit(SqlTask<?, ?> task);

    /**
     * Wraps the callback function and adds custom behavior to it.
     *
     * @param callback callback
     * @param <R>      the result type
     * @return wrapped callback with custom behavior
     * @throws NullPointerException if {@code callback} is null
     */
    <R> BiConsumer<? super R, ? super Throwable> wrapCallback(
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        return Objects.requireNonNull(callback);
    }

    /**
     * Converts a {@code Consumer} to a {@code BiConsumer} which ignores its
     * first argument.
     *
     * @param consumer Consumer that is converted
     * @return a BiConsumer that ignores its first argument
     * @throws NullPointerException if {@code consumer} is null
     */
    static BiConsumer<? super Void, ? super Throwable> toBiConsumer(
            Consumer<? super Throwable> consumer
    ) {
        Objects.requireNonNull(consumer);
        return (o, throwable) -> consumer.accept(throwable);
    }
}
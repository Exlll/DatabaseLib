package de.exlll.databaselib.submit;

import java.sql.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract class SqlTaskSubmitter {
    /**
     * Submits a new {@code SqlConnectionTask} that doesn't return a result.
     * <p>
     * The {@code Connection} is closed automatically after execution.
     * However, you are responsible for closing {@code Statement}s
     * you created from the connection.
     *
     * @param consumer Consumer that defines the operations on the Connection.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final void submitConnectionTask(
            CheckedSqlConsumer<Connection> consumer,
            Consumer<Exception> callback) {
        submit(newConnectionTask(consumer, callback));
    }

    /**
     * Submits a new {@code SqlConnectionTask} that returns a result.
     * <p>
     * The {@code Connection} is closed automatically after execution.
     * However, you are responsible for closing {@code Statement}s
     * you created from the connection.
     *
     * @param function Function that defines the operations on the Connection and
     *                 returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> void submitConnectionTask(
            CheckedSqlFunction<Connection, R> function,
            BiConsumer<? super R, Exception> callback) {
        submit(newConnectionTask(function, callback));
    }

    /**
     * Submits a new {@code SqlStatementTask} that doesn't return a result.
     * <p>
     * The {@code Statement} is closed automatically after execution.
     *
     * @param consumer Consumer that defines the operations on the Statement.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final void submitStatementTask(
            CheckedSqlConsumer<Statement> consumer,
            Consumer<Exception> callback) {
        submit(newStatementTask(consumer, callback));
    }

    /**
     * Submits a new {@code SqlStatementTask} that returns a result.
     * <p>
     * The {@code Statement} is closed automatically after execution.
     *
     * @param function Function that defines the operations on the Statement and
     *                 returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> void submitStatementTask(
            CheckedSqlFunction<Statement, R> function,
            BiConsumer<? super R, Exception> callback) {
        submit(newStatementTask(function, callback));
    }

    /**
     * Submits a new {@code SqlPreparedStatementTask} that doesn't return a result.
     * <p>
     * The {@code PreparedStatement} is closed automatically after execution.
     *
     * @param query    The query string which is prepared.
     * @param consumer Consumer that defines the operations on the PreparedStatement.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final void submitPreparedStatementTask(
            String query,
            CheckedSqlConsumer<PreparedStatement> consumer,
            Consumer<Exception> callback) {
        submit(newPreparedStatementTask(query, consumer, callback));
    }

    /**
     * Submits a new {@code SqlPreparedStatementTask} that returns a result.
     * <p>
     * The {@code PreparedStatement} is closed automatically after execution.
     *
     * @param query    The query string which is prepared.
     * @param function Function that defines the operations on the PreparedStatement
     *                 and returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> void submitPreparedStatementTask(
            String query,
            CheckedSqlFunction<PreparedStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        submit(newPreparedStatementTask(query, function, callback));
    }

    /**
     * Submits a new {@code SqlCallableStatementTask} that doesn't return a result.
     * <p>
     * The {@code CallableStatement} is closed automatically after execution.
     *
     * @param query    The query string which is prepared.
     * @param consumer Consumer that defines the operations on the CallableStatement.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final void submitCallableStatementTask(
            String query,
            CheckedSqlConsumer<CallableStatement> consumer,
            Consumer<Exception> callback) {
        submit(newCallableStatementTask(query, consumer, callback));
    }

    /**
     * Submits a new {@code SqlCallableStatementTask} that returns a result.
     * <p>
     * The {@code CallableStatement} is closed automatically after execution.
     *
     * @param query    The query string which is prepared.
     * @param function Function that defines the operations on the CallableStatement
     *                 and returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> void submitCallableStatementTask(
            String query,
            CheckedSqlFunction<CallableStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        submit(newCallableStatementTask(query, function, callback));
    }

    /**
     * Creates a new {@code SqlConnectionTask} that doesn't return a result when executed.
     * <p>
     * The {@code Connection} is closed automatically after execution.
     * However, you are responsible for closing {@code Statement}s
     * you created from the connection.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param consumer Consumer that defines the operations on the Connection.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @return new {@code SqlConnectionTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final SqlConnectionTask<Void> newConnectionTask(
            CheckedSqlConsumer<Connection> consumer,
            Consumer<Exception> callback) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(callback);
        return new SqlConnectionTask<>(CheckedSqlFunction.from(consumer), toBiConsumer(callback));
    }

    /**
     * Creates a new {@code SqlConnectionTask} that returns a result when executed.
     * <p>
     * The {@code Connection} is closed automatically after execution.
     * However, you are responsible for closing {@code Statement}s
     * you created from the connection.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param function Function that defines the operations on the Connection and
     *                 returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @return new {@code SqlConnectionTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> SqlConnectionTask<R> newConnectionTask(
            CheckedSqlFunction<Connection, R> function,
            BiConsumer<? super R, Exception> callback) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(callback);
        return new SqlConnectionTask<>(function, callback);
    }

    /**
     * Creates a new {@code SqlStatementTask} that doesn't return a result when executed.
     * <p>
     * The {@code Statement} is closed automatically after execution.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param consumer Consumer that defines the operations on the Statement.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @return new {@code SqlStatementTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final SqlStatementTask<Void> newStatementTask(
            CheckedSqlConsumer<Statement> consumer,
            Consumer<Exception> callback) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(callback);
        return new SqlStatementTask<>(CheckedSqlFunction.from(consumer), toBiConsumer(callback));
    }

    /**
     * Creates a new {@code SqlStatementTask} that returns a result when executed.
     * <p>
     * The {@code Statement} is closed automatically after execution.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param function Function that defines the operations on the Statement and
     *                 returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @return new {@code SqlStatementTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> SqlStatementTask<R> newStatementTask(
            CheckedSqlFunction<Statement, R> function,
            BiConsumer<? super R, Exception> callback) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(callback);
        return new SqlStatementTask<>(function, callback);
    }

    /**
     * Creates a new {@code SqlPreparedStatementTask} that doesn't return a result when executed.
     * <p>
     * The {@code PreparedStatement} is closed automatically after execution.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param query    The query string which is prepared.
     * @param consumer Consumer that defines the operations on the PreparedStatement.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @return new {@code SqlPreparedStatementTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final SqlPreparedStatementTask<Void> newPreparedStatementTask(
            String query,
            CheckedSqlConsumer<PreparedStatement> consumer,
            Consumer<Exception> callback) {
        Objects.requireNonNull(query);
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(callback);
        return new SqlPreparedStatementTask<>(
                query, CheckedSqlFunction.from(consumer), toBiConsumer(callback)
        );
    }

    /**
     * Creates a new {@code SqlPreparedStatementTask} that returns a result when executed.
     * <p>
     * The {@code PreparedStatement} is closed automatically after execution.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param query    The query string which is prepared.
     * @param function Function that defines the operations on the PreparedStatement
     *                 and returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @return new {@code SqlPreparedStatementTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> SqlPreparedStatementTask<R> newPreparedStatementTask(
            String query,
            CheckedSqlFunction<PreparedStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        Objects.requireNonNull(query);
        Objects.requireNonNull(function);
        Objects.requireNonNull(callback);
        return new SqlPreparedStatementTask<>(query, function, callback);
    }

    /**
     * Creates a new {@code SqlCallableStatementTask} that doesn't return a result when executed.
     * <p>
     * The {@code CallableStatement} is closed automatically after execution.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param query    The query string which is prepared.
     * @param consumer Consumer that defines the operations on the CallableStatement.
     * @param callback Callback that informs about exceptions. If no exceptions occurred,
     *                 the value accepted is null.
     * @return new {@code SqlConnectionTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final SqlCallableStatementTask<Void> newCallableStatementTask(
            String query,
            CheckedSqlConsumer<CallableStatement> consumer,
            Consumer<Exception> callback) {
        Objects.requireNonNull(query);
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(callback);
        return new SqlCallableStatementTask<>(
                query, CheckedSqlFunction.from(consumer), toBiConsumer(callback)
        );
    }

    /**
     * Creates a new {@code SqlCallableStatementTask} that returns a result when executed.
     * <p>
     * The {@code CallableStatement} is closed automatically after execution.
     * <p>
     * You can configure the task using its setters before submitting it with
     * {@link #submit(SqlTask)}.
     *
     * @param query    The query string which is prepared.
     * @param function Function that defines the operations on the CallableStatement
     *                 and returns a result.
     * @param callback Callback that accepts the result and informs about exceptions.
     *                 If no exception occurred, the {@code Exception} is null and the result
     *                 (i.e. the first value) is non-null. Otherwise, it's the other way around.
     * @param <R>      Type of the value returned by the function and of the first argument
     *                 accepted by the callback.
     * @return new {@code SqlConnectionTask}
     * @throws NullPointerException if any of the arguments is null
     */
    protected final <R> SqlCallableStatementTask<R> newCallableStatementTask(
            String query,
            CheckedSqlFunction<CallableStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        Objects.requireNonNull(query);
        Objects.requireNonNull(function);
        Objects.requireNonNull(callback);
        return new SqlCallableStatementTask<>(query, function, callback);
    }

    /**
     * Submits an {@code SqlTask} for asynchronous execution.
     * <p>
     * The task is executed in whichever {@code Thread} the library chooses.
     *
     * @param task task that is submitted
     */
    protected abstract void submit(SqlTask<?, ?> task);

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

    protected static BiConsumer<Void, Exception> toBiConsumer(Consumer<Exception> callback) {
        return (ignored, exception) -> callback.accept(exception);
    }
}

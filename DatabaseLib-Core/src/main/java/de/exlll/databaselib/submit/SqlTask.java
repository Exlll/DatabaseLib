package de.exlll.databaselib.submit;

import java.sql.Connection;
import java.sql.Statement;
import java.util.function.BiConsumer;

/**
 * @param <T> type of the applied connection or statement
 * @param <R> type of the value returned
 */
public abstract class SqlTask<T, R> {
    protected static final CheckedSqlConsumer<? super Statement> DEFAULT_STATEMENT_CONFIGURATOR =
            statement -> statement.setQueryTimeout(5);
    protected final CheckedSqlFunction<T, R> function;
    protected final BiConsumer<? super R, Exception> callback;
    protected R result;
    protected Exception exception;
    protected TaskPriority priority = TaskPriority.NORMAL;

    public SqlTask(CheckedSqlFunction<T, R> function,
                   BiConsumer<? super R, Exception> callback) {
        this.function = function;
        this.callback = callback;
    }

    public abstract void execute(Connection connection);

    public final void finish() {
        callback.accept(result, exception);
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public SqlTask<T, R> setPriority(TaskPriority priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public String toString() {
        return "SqlTask{" +
                "result=" + result +
                ", exception=" + exception +
                ", priority=" + priority +
                '}';
    }
}

package de.exlll.databaselib.submit;

import java.sql.Connection;
import java.sql.Statement;
import java.util.function.BiConsumer;

public final class SqlStatementTask<R> extends SqlTask<Statement, R> {
    private int timeoutInSeconds = DEFAULT_QUERY_TIMEOUT;

    public SqlStatementTask(
            CheckedSqlFunction<Statement, R> function,
            BiConsumer<? super R, Exception> callback) {
        super(function, callback);
    }

    @Override
    public void execute(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            stmt.setQueryTimeout(timeoutInSeconds);
            result = function.apply(stmt);
        } catch (Exception e) {
            exception = e;
        }
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public SqlStatementTask<R> setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        return this;
    }

    @Override
    public SqlStatementTask<R> setPriority(TaskPriority priority) {
        super.setPriority(priority);
        return this;
    }

    @Override
    public String toString() {
        return "SqlStatementTask{" +
                "result=" + result +
                ", exception=" + exception +
                ", priority=" + priority +
                ", timeoutInSeconds=" + timeoutInSeconds +
                '}';
    }
}

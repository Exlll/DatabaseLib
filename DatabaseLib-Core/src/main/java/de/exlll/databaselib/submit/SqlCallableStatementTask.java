package de.exlll.databaselib.submit;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.function.BiConsumer;

public final class SqlCallableStatementTask<R> extends SqlTask<CallableStatement, R> {
    private final String query;
    private int timeoutInSeconds = DEFAULT_QUERY_TIMEOUT;

    public SqlCallableStatementTask(
            String query,
            CheckedSqlFunction<CallableStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        super(function, callback);
        this.query = query;
    }

    @Override
    public void execute(Connection connection) {
        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setQueryTimeout(timeoutInSeconds);
            result = function.apply(stmt);
        } catch (Exception e) {
            exception = e;
        }
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public SqlCallableStatementTask<R> setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        return this;
    }

    @Override
    public SqlCallableStatementTask<R> setPriority(TaskPriority priority) {
        super.setPriority(priority);
        return this;
    }

    @Override
    public String toString() {
        return "SqlCallableStatementTask{" +
                "result=" + result +
                ", exception=" + exception +
                ", query=" + query +
                ", priority=" + priority +
                ", timeoutInSeconds=" + timeoutInSeconds +
                '}';
    }
}

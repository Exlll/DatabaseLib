package de.exlll.databaselib.submit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.BiConsumer;

public final class SqlPreparedStatementTask<R> extends SqlTask<PreparedStatement, R> {
    private final String query;
    private int timeoutInSeconds = DEFAULT_QUERY_TIMEOUT;

    public SqlPreparedStatementTask(
            String query,
            CheckedSqlFunction<PreparedStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        super(function, callback);
        this.query = query;
    }

    @Override
    public void execute(Connection connection) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setQueryTimeout(timeoutInSeconds);
            result = function.apply(stmt);
        } catch (Exception e) {
            exception = e;
        }
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public SqlPreparedStatementTask<R> setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        return this;
    }

    @Override
    public SqlPreparedStatementTask<R> setPriority(TaskPriority priority) {
        super.setPriority(priority);
        return this;
    }

    @Override
    public String toString() {
        return "SqlPreparedStatementTask{" +
                "result=" + result +
                ", exception=" + exception +
                ", query=" + query +
                ", priority=" + priority +
                ", timeoutInSeconds=" + timeoutInSeconds +
                '}';
    }
}

package de.exlll.databaselib.submit;

import java.sql.Connection;
import java.sql.Statement;
import java.util.function.BiConsumer;

public final class SqlStatementTask<R> extends SqlTask<Statement, R> {
    public SqlStatementTask(
            CheckedSqlFunction<Statement, R> function,
            BiConsumer<? super R, Exception> callback) {
        super(function, callback);
    }

    @Override
    public void execute(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            DEFAULT_STATEMENT_CONFIGURATOR.accept(stmt);
            result = function.apply(stmt);
        } catch (Exception e) {
            exception = e;
        }
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
                '}';
    }
}

package de.exlll.databaselib.submit;

import java.sql.Connection;
import java.util.function.BiConsumer;

public final class SqlConnectionTask<R> extends SqlTask<Connection, R> {
    public SqlConnectionTask(
            CheckedSqlFunction<Connection, R> function,
            BiConsumer<? super R, Exception> callback) {
        super(function, callback);
    }

    @Override
    public void execute(Connection connection) {
        try {
            result = function.apply(connection);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    public SqlConnectionTask<R> setPriority(TaskPriority priority) {
        super.setPriority(priority);
        return this;
    }

    @Override
    public String toString() {
        return "SqlConnectionTask{" +
                "result=" + result +
                ", exception=" + exception +
                ", priority=" + priority +
                '}';
    }
}

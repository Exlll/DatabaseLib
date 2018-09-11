package de.exlll.databaselib.sql.submit;

import java.sql.Connection;
import java.util.function.BiConsumer;

final class SqlConnectionTask<R> extends SqlTask<Connection, R> {
    SqlConnectionTask(
            CheckedSqlFunction<? super Connection, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        super(function, callback);
    }

    @Override
    void execute(Connection connection) {
        try {
            R result = function.apply(connection);
            callback.accept(result, null);
        } catch (Throwable e) {
            callback.accept(null, e);
        }
    }
}

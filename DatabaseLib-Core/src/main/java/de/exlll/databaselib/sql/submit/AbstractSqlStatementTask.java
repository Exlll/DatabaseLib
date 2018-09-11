package de.exlll.databaselib.sql.submit;

import java.sql.Connection;
import java.sql.Statement;
import java.util.function.BiConsumer;

abstract class AbstractSqlStatementTask<T extends Statement, R>
        extends SqlTask<T, R> {
    AbstractSqlStatementTask(
            CheckedSqlFunction<? super T, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        super(function, callback);
    }

    @Override
    final void execute(Connection connection) {
        try (T statement = statementFactory().apply(connection)) {
            R result = function.apply(statement);
            callback.accept(result, null);
        } catch (Throwable e) {
            callback.accept(null, e);
        }
    }

    abstract CheckedSqlFunction<? super Connection, ? extends T> statementFactory();
}

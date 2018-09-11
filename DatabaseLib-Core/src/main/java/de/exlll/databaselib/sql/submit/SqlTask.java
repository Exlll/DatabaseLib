package de.exlll.databaselib.sql.submit;

import java.sql.Connection;
import java.util.Objects;
import java.util.function.BiConsumer;


abstract class SqlTask<T, R> {
    final CheckedSqlFunction<? super T, ? extends R> function;
    final BiConsumer<? super R, ? super Throwable> callback;

    SqlTask(
            CheckedSqlFunction<? super T, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        this.function = Objects.requireNonNull(function);
        this.callback = Objects.requireNonNull(callback);
    }

    abstract void execute(Connection connection);
}

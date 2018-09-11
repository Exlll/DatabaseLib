package de.exlll.databaselib.sql.submit;

import java.sql.SQLException;
import java.util.Objects;

@FunctionalInterface
public interface CheckedSqlFunction<T, R> {
    R apply(T t) throws SQLException;

    static <T> CheckedSqlFunction<T, Void> from(
            CheckedSqlConsumer<? super T> consumer
    ) {
        Objects.requireNonNull(consumer);
        return t -> {
            consumer.accept(t);
            return null;
        };
    }
}

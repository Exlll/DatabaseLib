package de.exlll.databaselib.submit;

import java.sql.SQLException;
import java.util.Objects;

@FunctionalInterface
public interface CheckedSqlFunction<T, R> {
    R apply(T t) throws SQLException;

    static <T> CheckedSqlFunction<T, Void> from(CheckedSqlConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        return t -> {
            consumer.accept(t);
            return null;
        };
    }
}
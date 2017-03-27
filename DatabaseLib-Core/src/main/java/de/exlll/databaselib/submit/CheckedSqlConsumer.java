package de.exlll.databaselib.submit;

import java.sql.SQLException;

@FunctionalInterface
public interface CheckedSqlConsumer<T> {
    void accept(T t) throws SQLException;
}

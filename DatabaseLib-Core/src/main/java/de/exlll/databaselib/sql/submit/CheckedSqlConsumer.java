package de.exlll.databaselib.sql.submit;

import java.sql.SQLException;

@FunctionalInterface
public interface CheckedSqlConsumer<T> {
    void accept(T t) throws SQLException;
}

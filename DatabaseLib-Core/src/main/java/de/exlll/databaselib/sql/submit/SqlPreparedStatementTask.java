package de.exlll.databaselib.sql.submit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.BiConsumer;

final class SqlPreparedStatementTask<R>
        extends AbstractSqlStatementTask<PreparedStatement, R> {
    private final String query;

    SqlPreparedStatementTask(
            String query,
            CheckedSqlFunction<? super PreparedStatement, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        super(function, callback);
        this.query = query;
    }

    @Override
    CheckedSqlFunction<? super Connection, ? extends PreparedStatement> statementFactory() {
        return connection -> connection.prepareStatement(query);
    }
}

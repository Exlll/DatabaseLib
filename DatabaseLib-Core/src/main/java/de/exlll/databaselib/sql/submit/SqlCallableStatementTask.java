package de.exlll.databaselib.sql.submit;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.function.BiConsumer;

final class SqlCallableStatementTask<R>
        extends AbstractSqlStatementTask<CallableStatement, R> {
    private final String query;

    SqlCallableStatementTask(
            String query,
            CheckedSqlFunction<? super CallableStatement, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        super(function, callback);
        this.query = query;
    }

    @Override
    CheckedSqlFunction<? super Connection, ? extends CallableStatement> statementFactory() {
        return connection -> connection.prepareCall(query);
    }
}

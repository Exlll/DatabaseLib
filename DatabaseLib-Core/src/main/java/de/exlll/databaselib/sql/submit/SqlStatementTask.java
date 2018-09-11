package de.exlll.databaselib.sql.submit;

import java.sql.Connection;
import java.sql.Statement;
import java.util.function.BiConsumer;

final class SqlStatementTask<R>
        extends AbstractSqlStatementTask<Statement, R> {
    private static final CheckedSqlFunction<Connection, Statement> statementFactory =
            Connection::createStatement;

    SqlStatementTask(
            CheckedSqlFunction<? super Statement, ? extends R> function,
            BiConsumer<? super R, ? super Throwable> callback
    ) {
        super(function, callback);
    }

    @Override
    CheckedSqlFunction<? super Connection, ? extends Statement> statementFactory() {
        return statementFactory;
    }
}

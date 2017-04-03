package de.exlll.databaselib.submit;

import de.exlll.databaselib.submit.configure.PreparationStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.BiConsumer;

public final class SqlPreparedStatementTask<R> extends SqlTask<PreparedStatement, R> {
    private final String query;
    private PreparationStrategy preparationStrategy = PreparationStrategy.DEFAULT;

    public SqlPreparedStatementTask(
            String query,
            CheckedSqlFunction<PreparedStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        super(function, callback);
        this.query = query;
    }

    @Override
    public void execute(Connection connection) {
        try (PreparedStatement stmt = preparationStrategy.prepare(connection, query)) {
            DEFAULT_STATEMENT_CONFIGURATOR.accept(stmt);
            result = function.apply(stmt);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    public SqlPreparedStatementTask<R> setPriority(TaskPriority priority) {
        super.setPriority(priority);
        return this;
    }

    public PreparationStrategy getPreparationStrategy() {
        return preparationStrategy;
    }

    public SqlPreparedStatementTask<R> setPreparationStrategy(
            PreparationStrategy preparationStrategy) {
        this.preparationStrategy = preparationStrategy;
        return this;
    }

    @Override
    public String toString() {
        return "SqlPreparedStatementTask{" +
                "result=" + result +
                ", exception=" + exception +
                ", query=" + query +
                ", priority=" + priority +
                '}';
    }
}

package de.exlll.databaselib.submit;

import de.exlll.asynclib.exec.TaskPriority;
import de.exlll.databaselib.submit.configure.PreparationStrategy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.function.BiConsumer;

public final class SqlCallableStatementTask<R> extends SqlTask<CallableStatement, R> {
    private final String query;
    private PreparationStrategy<CallableStatement> preparationStrategy =
            PreparationStrategy.CALLABLE_STATEMENT_DEFAULT;

    public SqlCallableStatementTask(
            String query,
            CheckedSqlFunction<CallableStatement, R> function,
            BiConsumer<? super R, Exception> callback) {
        super(function, callback);
        this.query = query;
    }

    @Override
    public void execute(Connection connection) {
        try (CallableStatement stmt = preparationStrategy.prepare(connection, query)) {
            DEFAULT_STATEMENT_CONFIGURATOR.accept(stmt);
            result = function.apply(stmt);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    public SqlCallableStatementTask<R> setPriority(TaskPriority priority) {
        super.setPriority(priority);
        return this;
    }

    public SqlCallableStatementTask<R> setPreparationStrategy(
            PreparationStrategy<CallableStatement> preparationStrategy) {
        this.preparationStrategy = preparationStrategy;
        return this;
    }

    @Override
    public String toString() {
        return "SqlCallableStatementTask{" +
                "result=" + result +
                ", exception=" + exception +
                ", query=" + query +
                ", priority=" + priority +
                '}';
    }
}

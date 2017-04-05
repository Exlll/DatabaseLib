package de.exlll.databaselib.example;

import de.exlll.databaselib.PluginInfo;
import de.exlll.databaselib.submit.*;
import de.exlll.databaselib.submit.configure.PreparationStrategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

final class ExampleSubmitter extends BukkitSqlTaskSubmitter {
    public ExampleSubmitter(PluginInfo pluginInfo) {
        super(pluginInfo);
    }

    Consumer<Exception> exceptionCallback = exception ->
            pluginInfo.getLogger().log(Level.SEVERE, exception.getMessage(), exception);

    BiConsumer<Object, Exception> resultExceptionCallback = (result, exception) -> {
        if (exception != null) {
            pluginInfo.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        } else {
            pluginInfo.getLogger().info("result: " + result);
        }
    };

    public void submittingTasksWhichDoNotReturnResults() {
        submitConnectionTask(connection -> {
            // ...do something with the connection
        }, exceptionCallback);

        submitStatementTask(statement -> {
            // ...do something with the statement
        }, exceptionCallback);

        String query = "UPDATE ...";
        submitPreparedStatementTask(query, preparedStatement -> {
            // ...do something with the preparedStatement
        }, exceptionCallback);

        String call = "{call ... }";
        submitCallableStatementTask(call, callableStatement -> {
            // ...do something with the callableStatement
        }, exceptionCallback);
    }

    public void submittingTasksWhichReturnResults() {
        submitConnectionTask(connection -> {
            // ...do something with the connection
            return "1";
        }, resultExceptionCallback);

        submitStatementTask(statement -> {
            // ...do something with the statement
            return "2";
        }, resultExceptionCallback);

        String query = "SELECT * FROM ...";
        submitPreparedStatementTask(query, preparedStatement -> {
            // ...do something with the preparedStatement
            return "3";
        }, resultExceptionCallback);

        String call = "{call ... }";
        submitCallableStatementTask(call, callableStatement -> {
            // ...do something with the callableStatement
            return "4";
        }, resultExceptionCallback);
    }

    public void creatingAndConfiguringTasksBeforeSubmission() {
        // #####################
        // ### SqlConnectionTask
        // #####################

        // SqlConnectionTask that doesn't return a result
        SqlConnectionTask<Void> voidConnectionTask = newConnectionTask(connection -> {
            // ...do something with the connection
        }, exceptionCallback);

        voidConnectionTask.setPriority(TaskPriority.LOW);
        submit(voidConnectionTask);


        // SqlConnectionTask that returns a result
        SqlConnectionTask<String> resultConnectionTask = newConnectionTask(connection -> {
            // ...do something with the connection
            return "1";
        }, resultExceptionCallback);

        resultConnectionTask.setPriority(TaskPriority.HIGH);
        submit(resultConnectionTask);


        // ####################
        // ### SqlStatementTask
        // ####################

        // SqlStatementTask that doesn't return a result
        SqlStatementTask<Void> voidStatementTask = newStatementTask(statement -> {
            // ...do something with the statement
        }, exceptionCallback);

        voidStatementTask.setPriority(TaskPriority.LOW);
        submit(voidStatementTask);


        // SqlStatementTask that returns a result
        SqlStatementTask<String> resultStatementTask = newStatementTask(statement -> {
            // ...do something with the statement
            return "2";
        }, resultExceptionCallback);

        resultStatementTask.setPriority(TaskPriority.HIGH);
        submit(resultStatementTask);


        // ############################
        // ### SqlPreparedStatementTask
        // ############################

        // SqlPreparedStatementTask that doesn't return a result
        SqlPreparedStatementTask<Void> voidPreparedStatementTask =
                newPreparedStatementTask("UPDATE ...", preparedStatement -> {
                    // ...do something with the preparedStatement
                }, exceptionCallback);

        voidPreparedStatementTask
                .setPreparationStrategy(PreparationStrategy.PREPARED_STATEMENT_DEFAULT)
                .setPriority(TaskPriority.LOW);
        submit(voidPreparedStatementTask);


        // SqlPreparedStatementTask that returns a result
        SqlPreparedStatementTask<String> resultPreparedStatementTask =
                newPreparedStatementTask("SELECT ...", preparedStatement -> {
                    // ...do something with the preparedStatement
                    return "3";
                }, resultExceptionCallback);

        resultPreparedStatementTask
                .setPreparationStrategy(
                        PreparationStrategy.withAutoGeneratedKeys(Statement.RETURN_GENERATED_KEYS)
                )
                .setPriority(TaskPriority.HIGH);
        submit(resultPreparedStatementTask);


        // ############################
        // ### SqlCallableStatementTask
        // ############################

        // SqlCallableStatementTask that doesn't return a result
        SqlCallableStatementTask<Void> voidCallableStatementTask =
                newCallableStatementTask("{call ...}", callableStatement -> {
                    // ...do something with the callableStatement
                }, exceptionCallback);

        voidCallableStatementTask
                .setPreparationStrategy(PreparationStrategy.CALLABLE_STATEMENT_DEFAULT)
                .setPriority(TaskPriority.LOW);
        submit(voidCallableStatementTask);


        // SqlCallableStatementTask that returns a result
        SqlCallableStatementTask<String> resultCallableStatementTask =
                newCallableStatementTask("{call ...}", callableStatement -> {
                    // ...do something with the callableStatement
                    return "4";
                }, resultExceptionCallback);

        resultCallableStatementTask
                .setPreparationStrategy(PreparationStrategy.CALLABLE_STATEMENT_DEFAULT)
                .setPriority(TaskPriority.HIGH);
        submit(resultCallableStatementTask);
    }

    public void usingConnectionsDirectly() {
        try (Connection connection = getConnection()) {
            // ...do something with the connection
        } catch (SQLException e) {
            pluginInfo.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }
}

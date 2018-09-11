package de.exlll.databaselib.example.sql;

import de.exlll.databaselib.sql.pool.SqlConnectionPool;
import de.exlll.databaselib.sql.submit.PluginSqlTaskSubmitter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ExamplePlugin extends JavaPlugin {
    private final Logger log = Logger.getLogger(ExamplePlugin.class.getName());
    private final Consumer<Throwable> exceptionLogger = throwable -> {
        if (throwable != null) {
            log.log(Level.SEVERE, throwable.getMessage(), throwable);
        }
    };
    private UserRepository userRepository;

    public void deleteUser(UUID uuid) {
        userRepository.deleteUser(uuid, exceptionLogger);
    }

    public void updateUser(UUID uuid, String email) {
        userRepository.updateUserMail(uuid, email, exceptionLogger);
    }

    public void getUser(UUID uuid) {
        userRepository.getUser(uuid, (user, throwable) -> {
            if (throwable != null) {
                exceptionLogger.accept(throwable);
            } else {
                log.info("User: " + user);
            }
        });
    }

    @Override
    public void onEnable() {
        userRepository = new UserRepository(this);
        userRepository.createTable();
    }
}

final class UserRepository extends PluginSqlTaskSubmitter {
    public UserRepository(JavaPlugin plugin) {
        super(plugin);
    }

    public void deleteUser(UUID uuid, Consumer<Throwable> callback) {
        String query = "DELETE FROM `users` WHERE `uuid` = ?";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.execute();
        }, callback);
    }

    public void updateUserMail(UUID uuid, String email, Consumer<Throwable> callback) {
        String query = "UPDATE `users` SET `email` = ? WHERE `uuid` = ?";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        }, callback);
    }

    public void getUser(UUID uuid, BiConsumer<User, Throwable> callback) {
        String query = "SELECT * FROM `users` WHERE `uuid` = ?";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next()
                    ? new User(uuid, rs.getString("email"))
                    : null;
        }, callback);
    }

    public void createTable() {
        // get Connection synchronously, blocks if no Connection available
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS `users` (" +
                    "`uuid` VARCHAR(36) PRIMARY KEY," +
                    "`email` VARCHAR(36))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static class User {
        private final UUID uuid;
        private final String email;

        User(UUID uuid, String email) {
            this.uuid = uuid;
            this.email = email;
        }
    }
}

final class ExampleSubmitter extends PluginSqlTaskSubmitter {
    private final Consumer<Throwable> exceptionLogger = throwable -> {
        if (throwable != null) {
            plugin.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);
        }
    };
    private final BiConsumer<Object, Throwable> resultExceptionLogger =
            (result, throwable) -> {
                if (throwable != null) {
                    plugin.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);
                } else {
                    plugin.getLogger().info("result: " + result);
                }
            };

    public ExampleSubmitter(JavaPlugin plugin) {
        super(plugin);
    }

    /* You can provide your own SqlConnectionPool */
    public ExampleSubmitter(JavaPlugin plugin, SqlConnectionPool connectionPool) {
        super(plugin, connectionPool);
    }

    public void submittingTasksWhichDoNotReturnResults() {
        submitSqlStatementTask(connection -> {
            // ...do something with the connection
        }, exceptionLogger);

        submitSqlStatementTask(statement -> {
            // ...do something with the statement
        }, exceptionLogger);

        String query = "UPDATE ...";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            // ...do something with the preparedStatement
        }, exceptionLogger);

        String call = "{call ... }";
        submitSqlCallableStatementTask(call, callableStatement -> {
            // ...do something with the callableStatement
        }, exceptionLogger);
    }

    public void submittingTasksWhichReturnResults() {
        submitSqlConnectionTask(connection -> {
            // ...do something with the connection
            return "1";
        }, resultExceptionLogger);

        submitSqlStatementTask(statement -> {
            // ...do something with the statement
            return "2";
        }, resultExceptionLogger);

        String query = "SELECT * FROM ...";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            // ...do something with the preparedStatement
            return "3";
        }, resultExceptionLogger);

        String call = "{call ... }";
        submitSqlCallableStatementTask(call, callableStatement -> {
            // ...do something with the callableStatement
            return "4";
        }, resultExceptionLogger);
    }
}
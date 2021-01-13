# DatabaseLib v3
## How-to
#### Installation
1. Put the .jar in the plugin folder
2. Start the server
3. Stop the server
4. Configure the config.yml and sql_pool.yml
5. Start the server again
#### Usage
1. Create a new class (or use a preexisting one) and `extend  PluginSqlTaskSubmitter`.
2. Create a new instance of your class by either passing a `JavaPlugin` (Bukkit) or a
`Plugin` instance (BungeeCord).

Take a look at the examples below for a complete example of a Bukkit plugin.
## General information
#### Callbacks
When you create or submit tasks, you have to pass a non-null callback.
The type of the callback depends on whether the asynchronously executed method
returns something or not. If the method doesn't return anything (i.e. there is no
`return` statement), the type of the callback is `Consumer<Throwable>`.
Otherwise, it is `BiConsumer<ReturnType, Throwable>`.

Some examples:
- if there is no `return` statement, pass a `Consumer<Throwable>`
- if you return an `Integer`, pass a `BiConsumer<Integer, Throwable>`
- if you return a `String`, pass a `BiConsumer<String, Throwable>`
- in general: if you return something of type `R`, pass a `BiConsumer<? super R, Throwable>`

<details>
 <summary>Usage example</summary>

```java
public static final class UserRepo extends PluginSqlTaskSubmitter {
    public UserRepo(JavaPlugin plugin) { super(plugin); }

    public void deleteUser(UUID uuid, Consumer<Throwable> callback) {
        String query = "DELETE FROM `users` WHERE `uuid` = ?";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.execute();
        }, callback);
    }

    public void getUser(UUID uuid, BiConsumer<User, Throwable> callback) {
        String query = "SELECT * FROM `users` WHERE `uuid` = ?";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next()
                    ? new User(uuid.toString(), rs.getString("email"))
                    : null;
        }, callback);
    }
}
```
</details>

#### Task submission without callbacks (new in 3.1.0)
You can submit tasks which don't require a callback method and which instead return
a `CompletionStage`. The result of the `CompletionStage` is the result of the
function that is given when the task is submitted.

<details>
 <summary>Usage example</summary>

```java
public static final class UserRepo extends PluginSqlTaskSubmitter {
    public UserRepo(JavaPlugin plugin) { super(plugin); }

    public CompletionStage<List<User>> getUsers() {
        String sql = "SELECT * FROM `users`";
        return submitSqlPreparedStatementTask(sql, preparedStatement -> {
            List<User> users = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String name = resultSet.getString("name");
                users.add(new User(uuid, name));
            }
            return users;
        });
    }
}
```
</details>

#### Asynchronous execution of tasks
All tasks that are submitted through one of the different `submit...` methods
are executed asynchronously in whichever thread the library chooses. After
a task completes (either normally or by throwing an exception), its
callback method is executed in the server thread (Bukkit plugins) or in one
of the plugin threads (Bungee plugins). If the task completes
exceptionally, the `Throwable` passed to the callback is non-null.

#### Closing Connections and Statements
All library supplied `Connection`s and `Statement`s are closed automatically,
so you don't have to call `close()` on them. However, you still need to close
all `Statement`s you created yourself (e.g. when using an `SqlConnectionTask`).
The only exception to this rule is when you use `getConnection()` (see next section).

#### Synchronous execution of queries
Sometimes you don't want to execute queries asynchronously. In these cases
you can get a `Connection` directly from the pool by calling `getConnection()`.

**You are responsible for closing the connection after usage.** If you forget
to do so, the pool will run out of `Connection`s, so it's best to use a try-with-resources
block wherever possible when acquiring them this way.

**Be aware that a call to `getConnection()` is blocking.** If no `Connection` is available
(e.g. because of the pool being empty), the thread in which this method
is called will be blocked.

**(new in 3.4.0)** Instead of using a call to `getConnection()` you can also use any of 
`apply...` methods. These methods call `getConnection()` for you but try to reduce
boilerplate by catching any occurring `SQLExceptions` and rethrowing them wrapped in 
`RuntimeExceptions`.

<details>
 <summary>Usage example</summary>

```java
String query = "CREATE TABLE IF NOT EXISTS users(" +
               "    uuid  VARCHAR(36) NOT NULL PRIMARY KEY," +
               "    email VARCHAR(36)" +
               ")";
boolean result = applyStatement(statement -> statement.execute(query));
```
</details>

#### Creating your own connection pools
If your application submits many long-running tasks, or you have some reason that
makes it necessary for you to manage your own set of `Connection`s, you can instantiate a
connection pool by using the static factory methods of the `SqlConnectionPool` class.
To use these methods you have to pass a `SqlPoolConfig` instance which can be created by
using a `SqlPoolConfig.Builder`. If you want your users to be able to manually configure
your pool from a file, you can use a `SqlPoolConfiguration` to store its options.

#### ScriptRunner
A `ScriptRunner` is a utility class that lets you execute SQL scripts. An SQL script is a
file that contains SQL queries delimited by ';' (semicolon). You can create a `ScriptRunner`
by passing an instance of a `Reader` and a `Connection` to its constructor. A `ScriptRunner`
has the ability to replace any part of a query prior to executing it.

<details>
 <summary>Usage example</summary>

```java
try (Reader reader = new FileReader("my_script.sql");
     Connection connection = getConnection()) {

    ScriptRunner runner = new ScriptRunner(reader, connection);
    runner.setReplacements(Map.of("%USER%", "user1"));
    runner.runScript();

} catch (IOException | SQLException e) {
    e.printStackTrace();
}
```
</details>

## Examples
#### Complete Bukkit plugin example
```java
import de.exlll.databaselib.sql.submit.PluginSqlTaskSubmitter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExamplePlugin extends JavaPlugin {
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

    public void getUserWithCompletionStage(UUID uuid) {
        userRepository.getUserWithCompletionStage(uuid)
                .whenComplete((user, throwable) -> {
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

    public CompletionStage<User> getUserWithCompletionStage(UUID uuid) {
        String query = "SELECT * FROM `users` WHERE `uuid` = ?";
        return submitSqlPreparedStatementTask(query, preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next()
                    ? new User(uuid, rs.getString("email"))
                    : null;
        });
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
```

#### Ways to submit tasks
```java
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

    public void submittingTasksWhichReturnCompletionStages() {
        submitSqlConnectionTask(connection -> {
            // ...do something with the connection
            return "1";
        });

        submitSqlStatementTask(statement -> {
            // ...do something with the statement
            return "2";
        });

        String query = "SELECT * FROM ...";
        submitSqlPreparedStatementTask(query, preparedStatement -> {
            // ...do something with the preparedStatement
            return "3";
        });

        String call = "{call ... }";
        submitSqlCallableStatementTask(call, callableStatement -> {
            // ...do something with the callableStatement
            return "4";
        });
    }
}
```
## Import
#### Maven
```xml
<repository>
    <id>de.exlll</id>
    <url>https://repo.terraconia.de/artifactory/terraconia-repos</url>
</repository>

<!-- for Bukkit plugins -->
<dependency>
    <groupId>de.exlll</groupId>
    <artifactId>databaselib-bukkit</artifactId>
    <version>3.4.0</version>
</dependency>

<!-- for Bungee plugins -->
<dependency>
    <groupId>de.exlll</groupId>
    <artifactId>databaselib-bungee</artifactId>
    <version>3.4.0</version>
</dependency>
```
#### Gradle
```groovy
repositories {
    maven {
        url 'https://repo.terraconia.de/artifactory/terraconia-repos'
    }
}
dependencies {
    // for Bukkit plugins
    compile group: 'de.exlll', name: 'databaselib-bukkit', version: '3.4.0'

    // for Bungee plugins
    compile group: 'de.exlll', name: 'databaselib-bungee', version: '3.4.0'
}
```
Additionally, you either have to import the Bukkit or BungeeCord API
or disable transitive lookups. This project uses both of these APIs, so if you
need an example of how to import them using Gradle, take a look at the `build.gradle`.

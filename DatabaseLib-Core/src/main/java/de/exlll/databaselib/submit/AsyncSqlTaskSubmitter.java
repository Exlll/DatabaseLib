package de.exlll.databaselib.submit;

import de.exlll.asynclib.exec.TaskExecutor;
import de.exlll.asynclib.task.AsyncTask;
import de.exlll.databaselib.pool.SqlConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

class AsyncSqlTaskSubmitter extends SqlTaskSubmitter {
    protected final PluginInfo pluginInfo;
    private final SqlConnectionPool connectionPool;
    private final TaskExecutor executor;

    AsyncSqlTaskSubmitter(
            PluginInfo pluginInfo,
            SqlConnectionPool connectionPool,
            TaskExecutor executor) {
        this.pluginInfo = pluginInfo;
        this.connectionPool = connectionPool;
        this.executor = executor;
    }

    @Override
    protected final void submit(SqlTask<?, ?> task) {
        executor.execute(new AsyncSqlTask(task), task.getPriority());
    }

    @Override
    protected final Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    private final class AsyncSqlTask implements AsyncTask {
        private final SqlTask<?, ?> sqlTask;

        private AsyncSqlTask(SqlTask<?, ?> sqlTask) {
            this.sqlTask = sqlTask;
        }

        @Override
        public void execute() {
            try (Connection connection = connectionPool.getConnection()) {
                sqlTask.execute(connection);
            } catch (SQLException e) {
                sqlTask.exception = e;
            }
        }

        @Override
        public void finish() {
            sqlTask.finish();
        }

        @Override
        public String toString() {
            return "AsyncSqlTask{" +
                    "pluginName=" + pluginInfo.getName() +
                    ", sqlTask=" + sqlTask +
                    '}';
        }
    }

    protected static final class PluginInfo {
        private final String pluginName;
        private final Logger pluginLogger;

        PluginInfo(String name, Logger logger) {
            this.pluginName = name;
            this.pluginLogger = logger;
        }

        public String getName() {
            return pluginName;
        }

        public Logger getLogger() {
            return pluginLogger;
        }
    }
}

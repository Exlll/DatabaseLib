package de.exlll.databaselib.submit;

import de.exlll.asynclib.service.PriorityTaskService;
import de.exlll.asynclib.task.AsyncTask;
import de.exlll.databaselib.PluginInfo;
import de.exlll.databaselib.pool.SqlConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

class AsyncSqlTaskSubmitter extends SqlTaskSubmitter {
    protected final PluginInfo pluginInfo;
    private final SqlConnectionPool connectionPool;
    private final PriorityTaskService priorityTaskService;

    AsyncSqlTaskSubmitter(
            PluginInfo pluginInfo,
            SqlConnectionPool connectionPool,
            PriorityTaskService priorityTaskService) {
        this.pluginInfo = pluginInfo;
        this.connectionPool = connectionPool;
        this.priorityTaskService = priorityTaskService;
    }

    @Override
    protected final void submit(SqlTask<?, ?> task) {
        int priority = task.getPriority().intValue();
        priorityTaskService.execute(new AsyncSqlTask(task), priority);
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
}

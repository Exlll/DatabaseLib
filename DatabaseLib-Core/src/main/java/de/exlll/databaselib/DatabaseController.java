package de.exlll.databaselib;

import de.exlll.asynclib.service.PriorityTaskService;
import de.exlll.asynclib.service.ServiceConfig;
import de.exlll.databaselib.pool.HikariConnectionPool;
import de.exlll.databaselib.pool.PoolConfig;
import de.exlll.databaselib.pool.SqlConnectionPool;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

final class DatabaseController {
    private final SqlConnectionPool connectionPool;
    private final PriorityTaskService priorityTaskService;
    private final PoolConfig poolConfig;
    private final AsyncConfig asyncConfig;

    DatabaseController(File dataFolder) {
        File poolConfigFile = new File(dataFolder, "pool.yml");
        File asyncConfigFile = new File(dataFolder, "async.yml");

        poolConfig = new PoolConfig(poolConfigFile.toPath());
        asyncConfig = new AsyncConfig(asyncConfigFile.toPath());

        try {
            poolConfig.loadAndSave();
            asyncConfig.loadAndSave();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectionPool = new HikariConnectionPool(poolConfig);
        priorityTaskService = new PriorityTaskService(asyncConfig.getConfig());
    }

    SqlConnectionPool getPool() {
        if (connectionPool == null) {
            throw new IllegalStateException("DatabaseLib pool not initialized.");
        }
        return connectionPool;
    }

    PriorityTaskService getService() {
        if (priorityTaskService == null) {
            throw new IllegalStateException("DatabaseLib service not initialized.");
        }
        return priorityTaskService;
    }

    PoolConfig getPoolConfig() {
        return poolConfig;
    }

    ServiceConfig getServiceConfig() {
        return asyncConfig.getConfig();
    }

    void stop() {
        if (connectionPool != null) {
            connectionPool.stop();
        }
        if (priorityTaskService != null) {
            priorityTaskService.shutdown(10, TimeUnit.SECONDS);
            priorityTaskService.finishAllTasks();
        }
    }
}

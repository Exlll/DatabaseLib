package de.exlll.databaselib;

import de.exlll.asynclib.exec.ServiceConfig;
import de.exlll.asynclib.exec.TaskService;
import de.exlll.asynclib.util.ServiceConfiguration;
import de.exlll.configlib.Configuration;
import de.exlll.databaselib.pool.HikariConnectionPool;
import de.exlll.databaselib.pool.PoolConfig;
import de.exlll.databaselib.pool.SqlConnectionPool;
import de.exlll.databaselib.util.PoolConfiguration;

import java.io.File;
import java.io.IOException;

abstract class DatabaseController {
    private final SqlConnectionPool pool;
    private final TaskService service;

    DatabaseController(File dataFolder) {
        PoolConfig poolConfig = new PoolConfig.Builder()
                .addDriverProperty("cachePrepStmts", "true")
                .addDriverProperty("cacheCallableStmts", "true")
                .addDriverProperty("cacheServerConfiguration", "true")
                .build();

        PoolConfiguration poolConfiguration = new PoolConfiguration(
                new File(dataFolder, "pool.yml").toPath(), poolConfig
        );
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration(
                new File(dataFolder, "async.yml").toPath()
        );

        loadConfiguration(poolConfiguration);
        loadConfiguration(serviceConfiguration);

        poolConfig = poolConfiguration.getConfig()
                .orElseThrow(IllegalStateException::new);
        ServiceConfig serviceConfig = serviceConfiguration.getConfig()
                .orElseThrow(IllegalStateException::new);

        this.pool = new HikariConnectionPool(poolConfig);
        this.service = createService(serviceConfig);
    }

    protected abstract TaskService createService(ServiceConfig config);

    private void loadConfiguration(Configuration cfg) {
        try {
            cfg.loadAndSave();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final SqlConnectionPool getPool() {
        return pool;
    }

    protected final TaskService getService() {
        return service;
    }

    protected final void stop() {
        if (pool != null) {
            pool.stop();
        }
        if (service != null) {
            service.stop();
        }
    }
}

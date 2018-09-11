package de.exlll.databaselib;

import de.exlll.databaselib.sql.pool.HikariConnectionPool;
import de.exlll.databaselib.sql.pool.SqlConnectionPool;
import de.exlll.databaselib.sql.pool.SqlPoolConfig;
import de.exlll.databaselib.sql.util.SqlPoolConfiguration;

import java.io.File;

final class SqlDatabaseService {
    private final SqlConnectionPool connectionPool;

    SqlDatabaseService(File dataFolder) {
        SqlPoolConfig poolConfig = SqlPoolConfig.builder()
                .addDriverProperty("cachePrepStmts", "true")
                .addDriverProperty("cacheCallableStmts", "true")
                .addDriverProperty("cacheServerConfiguration", "true")
                .build();

        SqlPoolConfiguration configuration = new SqlPoolConfiguration(
                new File(dataFolder, "sql_pool.yml").toPath(), poolConfig
        );
        configuration.loadAndSave();
        this.connectionPool = configuration.getConfig().
                map(HikariConnectionPool::new)
                .orElse(null);
    }

    SqlConnectionPool getConnectionPool() {
        return connectionPool;
    }

    void stop() {
        try {
            if (connectionPool != null) {
                connectionPool.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

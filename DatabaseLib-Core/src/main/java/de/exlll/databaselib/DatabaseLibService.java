package de.exlll.databaselib;

import de.exlll.databaselib.sql.pool.SqlConnectionPool;

import java.io.File;

final class DatabaseLibService {
    private final File dataFolder;
    private final DatabaseLibConfiguration libConfig;
    private SqlDatabaseService sqlDatabaseService;

    DatabaseLibService(File dataFolder) {
        this.dataFolder = dataFolder;
        this.libConfig = new DatabaseLibConfiguration(
                new File(dataFolder, "config.yml").toPath()
        );
        this.libConfig.loadAndSave();
    }

    void onEnable() {
        if (libConfig.isEnableSqlPool()) {
            sqlDatabaseService = new SqlDatabaseService(dataFolder);
        }
    }

    void onDisable() {
        if (sqlDatabaseService != null) {
            sqlDatabaseService.stop();
        }
    }

    SqlConnectionPool getSqlConnectionPool() {
        checkSqlConnectionPoolInitialized();
        return sqlDatabaseService.getConnectionPool();
    }

    private void checkSqlConnectionPoolInitialized() {
        if ((sqlDatabaseService == null) ||
                (sqlDatabaseService.getConnectionPool() == null)) {
            String msg = libConfig.isEnableSqlPool()
                    ? "The main sql connection pool has not been initialized yet."
                    : "The main sql connection pool is disabled.";
            throw new IllegalStateException(msg);
        }
    }
}

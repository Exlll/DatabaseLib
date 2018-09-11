package de.exlll.databaselib;

import de.exlll.databaselib.sql.pool.SqlConnectionPool;
import net.md_5.bungee.api.plugin.Plugin;

public final class DatabaseLib extends Plugin {
    private static DatabaseLibService databaseLibService;

    @Override
    public void onEnable() {
        databaseLibService = new DatabaseLibService(getDataFolder());
        databaseLibService.onEnable();
    }

    @Override
    public void onDisable() {
        databaseLibService.onDisable();
    }

    /**
     * Returns the main pool.
     *
     * @return the main pool
     * @throws IllegalStateException if main pool not initialized
     */
    public static SqlConnectionPool getMainPool() {
        checkDatabaseLibServiceInitialized();
        return databaseLibService.getSqlConnectionPool();
    }

    private static void checkDatabaseLibServiceInitialized() {
        if (databaseLibService == null) {
            String msg = "The DatabaseLib plugin has not been initialized yet.";
            throw new IllegalStateException(msg);
        }
    }
}

package de.exlll.databaselib;

import de.exlll.asynclib.exec.PluginTaskService;
import de.exlll.asynclib.exec.ServiceConfig;
import de.exlll.asynclib.exec.TaskExecutor;
import de.exlll.asynclib.exec.TaskService;
import de.exlll.databaselib.pool.SqlConnectionPool;
import net.md_5.bungee.api.plugin.Plugin;

public final class DatabaseLib extends Plugin {
    private static DatabaseController controller;

    @Override
    public void onEnable() {
        controller = new PluginDatabaseController();
    }

    @Override
    public void onDisable() {
        if (controller != null) {
            controller.stop();
        }
    }

    public static SqlConnectionPool getMainPool() {
        checkControllerState();
        return controller.getPool();
    }

    public static TaskExecutor getExecutor() {
        checkControllerState();
        return controller.getService();
    }

    private static void checkControllerState() {
        if (controller == null) {
            throw new IllegalStateException("DatabaseLib plugin not initialized.");
        }
    }

    private final class PluginDatabaseController extends DatabaseController {
        private PluginDatabaseController() {
            super(DatabaseLib.this.getDataFolder());
        }

        @Override
        protected TaskService createService(ServiceConfig config) {
            return new PluginTaskService(DatabaseLib.this, config);
        }
    }
}

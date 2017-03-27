package de.exlll.databaselib;

import de.exlll.asynclib.service.PriorityTaskService;
import de.exlll.databaselib.pool.SqlConnectionPool;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class DatabaseLib extends JavaPlugin {
    private static DatabaseController controller;

    @Override
    public void onEnable() {
        controller = new DatabaseController(getDataFolder());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                this,
                controller.getService()::finishTasks,
                0,
                controller.getServiceConfig().getPollPeriod()
        );
    }

    @Override
    public void onDisable() {
        if (controller != null) {
            controller.stop();
        }
    }

    public static SqlConnectionPool getPool() {
        checkControllerState();
        return controller.getPool();
    }

    public static PriorityTaskService getService() {
        checkControllerState();
        return controller.getService();
    }

    private static void checkControllerState() {
        if (controller == null) {
            throw new IllegalStateException("controller not initialized");
        }
    }

    public static PluginInfo fromPlugin(JavaPlugin plugin) {
        return new BukkitPluginInfo(plugin);
    }

    private static final class BukkitPluginInfo implements PluginInfo {
        private final JavaPlugin plugin;

        private BukkitPluginInfo(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String getName() {
            return plugin.getName();
        }

        @Override
        public Logger getLogger() {
            return plugin.getLogger();
        }
    }
}

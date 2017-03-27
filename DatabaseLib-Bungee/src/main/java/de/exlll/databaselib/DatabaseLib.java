package de.exlll.databaselib;

import de.exlll.asynclib.service.PriorityTaskService;
import de.exlll.databaselib.pool.SqlConnectionPool;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class DatabaseLib extends Plugin {
    private static DatabaseController controller;

    @Override
    public void onEnable() {
        controller = new DatabaseController(getDataFolder());

        ProxyServer.getInstance().getScheduler().schedule(
                this,
                controller.getService()::finishTasks,
                0,
                controller.getServiceConfig().getPollPeriod() * 50,
                TimeUnit.MILLISECONDS
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

    public static PluginInfo fromPlugin(Plugin plugin) {
        return new BungeePluginInfo(plugin);
    }

    private static final class BungeePluginInfo implements PluginInfo {
        private final Plugin plugin;

        private BungeePluginInfo(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String getName() {
            return plugin.getDescription().getName();
        }

        @Override
        public Logger getLogger() {
            return plugin.getLogger();
        }
    }
}

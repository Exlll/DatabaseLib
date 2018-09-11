package de.exlll.databaselib.sql.submit;

import de.exlll.databaselib.DatabaseLib;
import de.exlll.databaselib.sql.pool.SqlConnectionPool;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.function.BiConsumer;

public abstract class PluginSqlTaskSubmitter extends AsyncSqlTaskSubmitter {
    private static final BiConsumer<Plugin, Runnable> taskExecutor =
            (plugin, task) -> ProxyServer.getInstance()
                    .getScheduler()
                    .runAsync(plugin, task);
    protected final Plugin plugin;

    public PluginSqlTaskSubmitter(Plugin plugin) {
        this(plugin, DatabaseLib.getMainPool());
    }

    public PluginSqlTaskSubmitter(Plugin plugin, SqlConnectionPool connectionPool) {
        super(
                connectionPool,
                task -> taskExecutor.accept(plugin, task),
                task -> taskExecutor.accept(plugin, task)
        );
        this.plugin = plugin;
    }
}

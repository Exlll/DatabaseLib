package de.exlll.databaselib.sql.submit;

import de.exlll.databaselib.DatabaseLib;
import de.exlll.databaselib.sql.pool.SqlConnectionPool;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PluginSqlTaskSubmitter extends AsyncSqlTaskSubmitter {
    protected final JavaPlugin plugin;

    public PluginSqlTaskSubmitter(JavaPlugin plugin) {
        this(plugin, DatabaseLib.getMainPool());
    }

    public PluginSqlTaskSubmitter(JavaPlugin plugin, SqlConnectionPool connectionPool) {
        super(
                connectionPool,
                task -> Bukkit.getScheduler().runTask(plugin, task),
                task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
        );
        this.plugin = plugin;
    }
}

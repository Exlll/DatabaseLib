package de.exlll.databaselib.submit;

import de.exlll.databaselib.DatabaseLib;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginSqlTaskSubmitter extends AsyncSqlTaskSubmitter {
    public PluginSqlTaskSubmitter(JavaPlugin plugin) {
        super(fromPlugin(plugin), DatabaseLib.getMainPool(), DatabaseLib.getExecutor());
    }

    private static PluginInfo fromPlugin(JavaPlugin plugin) {
        return new PluginInfo(plugin.getName(), plugin.getLogger());
    }
}

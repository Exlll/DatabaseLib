package de.exlll.databaselib.submit;

import de.exlll.databaselib.DatabaseLib;
import net.md_5.bungee.api.plugin.Plugin;

public class PluginSqlTaskSubmitter extends AsyncSqlTaskSubmitter {
    public PluginSqlTaskSubmitter(Plugin plugin) {
        super(fromPlugin(plugin), DatabaseLib.getMainPool(), DatabaseLib.getExecutor());
    }

    private static PluginInfo fromPlugin(Plugin plugin) {
        final String name = plugin.getDescription().getName();
        return new PluginInfo(name, plugin.getLogger());
    }
}

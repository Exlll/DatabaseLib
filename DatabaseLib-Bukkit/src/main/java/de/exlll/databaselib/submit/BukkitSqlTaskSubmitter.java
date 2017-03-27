package de.exlll.databaselib.submit;

import de.exlll.databaselib.DatabaseLib;
import de.exlll.databaselib.PluginInfo;

public class BukkitSqlTaskSubmitter extends AsyncSqlTaskSubmitter {
    public BukkitSqlTaskSubmitter(PluginInfo pluginInfo) {
        super(pluginInfo, DatabaseLib.getPool(), DatabaseLib.getService());
    }
}

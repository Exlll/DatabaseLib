package de.exlll.databaselib.submit;

import de.exlll.databaselib.DatabaseLib;
import de.exlll.databaselib.PluginInfo;

public class BungeeSqlTaskSubmitter extends AsyncSqlTaskSubmitter {
    public BungeeSqlTaskSubmitter(PluginInfo pluginInfo) {
        super(pluginInfo, DatabaseLib.getPool(), DatabaseLib.getService());
    }
}

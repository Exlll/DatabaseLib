package de.exlll.databaselib;

import java.util.logging.Logger;

public interface PluginInfo {
    /**
     * Returns the name of the plugin.
     *
     * @return plugin name
     */
    String getName();

    /**
     * Returns the plugin logger.
     *
     * @return plugin logger
     */
    Logger getLogger();
}

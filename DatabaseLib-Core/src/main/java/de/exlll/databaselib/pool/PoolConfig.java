package de.exlll.databaselib.pool;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PoolConfig extends Configuration {
    private String username = "root";
    private String password = "";
    private String database = "minecraft";
    private String host = "localhost";
    private int port = 3306;
    @Comment("maximum number of open connections")
    private int maxPoolSize = 10;
    @Comment("minimum number of idle connections in the pool")
    private int minIdle = 2;
    @Comment({
            "various driver configuration properties",
            "https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html"
    })
    private Map<String, String> properties = new HashMap<>();

    /**
     * @param configPath Path of the configuration file
     * @see Configuration#Configuration(Path)
     */
    public PoolConfig(Path configPath) {
        super(configPath);
        properties.put("cachePrepStmts", "true");
        properties.put("cacheCallableStmts", "true");
        properties.put("cacheServerConfiguration", "true");
    }

    @Override
    protected void postLoadHook() {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("invalid port: " + port);
        }
        if (maxPoolSize < 1) {
            throw new IllegalArgumentException("maxPoolSize must be at least 1");
        }
        if (minIdle < 1) {
            throw new IllegalArgumentException("minIdle must be at least 1");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}

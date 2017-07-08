package de.exlll.databaselib.util;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.databaselib.pool.PoolConfig;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link Configuration} representing a {@link PoolConfig}
 */
public final class PoolConfiguration extends Configuration {
    private transient PoolConfig config;
    private String username;
    private String password;
    private String database;
    private String host;
    private int port;
    @Comment("The core pool size defines how many connections to " +
            "keep in the pool, even if they are idle.")
    private int corePoolSize;
    @Comment("The maximum pool size defines the maximum " +
            "number of connections the pool allows.")
    private int maximumPoolSize;
    @Comment({
            "Various driver configuration properties.",
            "For more information, please consult the official documentation:",
            "https://dev.mysql.com/doc/connector-j/5.1/en/" +
                    "connector-j-reference-configuration-properties.html"
    })
    private Map<String, String> driverProperties;

    /**
     * Creates a new {@code PoolConfiguration} that uses the default
     * {@link PoolConfig} for its default values.
     *
     * @param configPath location of the configuration file
     * @throws NullPointerException if {@code configPath} is null
     */
    public PoolConfiguration(Path configPath) {
        this(configPath, PoolConfig.DEFAULT);
    }

    /**
     * Creates a new {@code PoolConfiguration} that uses the given
     * {@link PoolConfig} for its default values.
     *
     * @param configPath location of the configuration file
     * @param config     {@code PoolConfig} used for default values
     * @throws NullPointerException if {@code configPath} or {@code config} is null
     */
    public PoolConfiguration(Path configPath, PoolConfig config) {
        super(configPath);
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.database = config.getDatabase();
        this.host = config.getHost();
        this.port = config.getPort();
        this.corePoolSize = config.getCorePoolSize();
        this.maximumPoolSize = config.getMaximumPoolSize();
        this.driverProperties = config.getDriverProperties();
    }

    @Override
    protected void postLoadHook() {
        config = new PoolConfig.Builder()
                .setUsername(username)
                .setPassword(password)
                .setDatabase(database)
                .setHost(host)
                .setPort(port)
                .setPoolSize(corePoolSize, maximumPoolSize)
                .setDriverProperties(driverProperties)
                .build();
    }

    /**
     * Returns an {@link Optional} describing the {@link PoolConfig}
     * represented by this {@code Configuration} or an empty {@code Optional}
     * if this {@code Configuration} has not (successfully) been loaded yet.
     *
     * @return {@code Optional} describing the {@code PoolConfig} represented
     * by this {@code Configuration} or an empty {@code Optional}.
     */
    public Optional<PoolConfig> getConfig() {
        return Optional.ofNullable(config);
    }
}

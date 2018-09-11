package de.exlll.databaselib.sql.util;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.Format;
import de.exlll.configlib.configs.yaml.YamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import de.exlll.databaselib.sql.pool.SqlPoolConfig;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link YamlConfiguration} representing an {@link SqlPoolConfig}
 */
@Format(FieldNameFormatters.LOWER_UNDERSCORE)
public final class SqlPoolConfiguration extends YamlConfiguration {
    private transient SqlPoolConfig config;
    @Comment("Protocol: 'mysql', 'mariadb' or 'postgresql'")
    private String protocol;
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
    @Comment("Various driver configuration properties.")
    private Map<String, String> driverProperties;

    /**
     * Creates a new {@code SqlPoolConfiguration} that uses the default
     * {@link SqlPoolConfig} for its default values.
     *
     * @param configPath location of the configuration file
     * @throws NullPointerException if {@code configPath} is null
     */
    public SqlPoolConfiguration(Path configPath) {
        this(configPath, SqlPoolConfig.DEFAULT);
    }

    /**
     * Creates a new {@code SqlPoolConfiguration} that uses the given
     * {@link SqlPoolConfig} for its default values.
     *
     * @param configPath location of the configuration file
     * @param config     {@code PoolConfig} used for default values
     * @throws NullPointerException if {@code configPath} or {@code config} is null
     */
    public SqlPoolConfiguration(Path configPath, SqlPoolConfig config) {
        super(configPath);
        this.protocol = config.getProtocol();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.database = config.getDatabase();
        this.host = config.getHost();
        this.port = config.getPort();
        this.corePoolSize = config.getCorePoolSize();
        this.maximumPoolSize = config.getMaximumPoolSize();
        this.driverProperties = config.getDriverProperties();
        this.config = config;
    }

    @Override
    protected void postLoad() {
        this.config = SqlPoolConfig.builder()
                .setProtocol(protocol)
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
     * Returns an {@link Optional} describing the {@link SqlPoolConfig}
     * represented by this {@code Configuration}.
     *
     * @return {@code Optional} describing the {@code PoolConfig} represented
     * by this {@code Configuration} or an empty {@code Optional}.
     */
    public Optional<SqlPoolConfig> getConfig() {
        return Optional.ofNullable(config);
    }
}

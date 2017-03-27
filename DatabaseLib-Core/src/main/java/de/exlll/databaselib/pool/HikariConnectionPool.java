package de.exlll.databaselib.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class HikariConnectionPool implements SqlConnectionPool {
    private final HikariDataSource dataSource;

    public HikariConnectionPool(PoolConfig poolConfig) {
        Objects.requireNonNull(poolConfig);
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" +
                poolConfig.getHost() + ":" +
                poolConfig.getPort() + "/" +
                poolConfig.getDatabase()
        );
        config.setUsername(poolConfig.getUsername());
        config.setPassword(poolConfig.getPassword());

        config.setMaximumPoolSize(poolConfig.getMaxPoolSize());
        config.setMinimumIdle(poolConfig.getMinIdle());
        poolConfig.getProperties().forEach(config::addDataSourceProperty);

        dataSource = new HikariDataSource(config);
    }

    @Override
    public void stop() {
        dataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

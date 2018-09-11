package de.exlll.databaselib.sql.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class HikariConnectionPool implements SqlConnectionPool {
    private final HikariDataSource dataSource;

    public HikariConnectionPool(SqlPoolConfig sqlPoolConfig) {
        Objects.requireNonNull(sqlPoolConfig);
        HikariConfig config = createHikariConfig(sqlPoolConfig);
        dataSource = new HikariDataSource(config);
    }

    private HikariConfig createHikariConfig(SqlPoolConfig sqlPoolConfig) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:" +
                sqlPoolConfig.getProtocol() + "://" +
                sqlPoolConfig.getHost() + ":" +
                sqlPoolConfig.getPort() + "/" +
                sqlPoolConfig.getDatabase()
        );
        config.setUsername(sqlPoolConfig.getUsername());
        config.setPassword(sqlPoolConfig.getPassword());

        config.setMaximumPoolSize(sqlPoolConfig.getMaximumPoolSize());
        config.setMinimumIdle(sqlPoolConfig.getCorePoolSize());
        sqlPoolConfig.getDriverProperties().forEach(config::addDataSourceProperty);
        return config;
    }


    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

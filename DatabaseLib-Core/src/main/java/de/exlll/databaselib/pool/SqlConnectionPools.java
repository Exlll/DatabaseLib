package de.exlll.databaselib.pool;

import java.util.Objects;

public enum SqlConnectionPools {
    ;

    public static SqlConnectionPool newDefaultPool(PoolConfig config) {
        Objects.requireNonNull(config);
        return new HikariConnectionPool(config);
    }
}

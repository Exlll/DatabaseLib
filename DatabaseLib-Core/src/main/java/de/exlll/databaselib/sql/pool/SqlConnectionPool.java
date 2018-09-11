package de.exlll.databaselib.sql.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@FunctionalInterface
public interface SqlConnectionPool extends AutoCloseable {
    /**
     * Returns a {@code Connection} from the pool.
     * <p>
     * The connection must manually be closed after usage.
     *
     * @return {@code Connection} from pool
     * @throws SQLException if a database access error occurs
     */
    Connection getConnection() throws SQLException;

    @Override
    default void close() throws Exception {}

    static SqlConnectionPool newDefaultPool(SqlPoolConfig config) {
        Objects.requireNonNull(config);
        return new HikariConnectionPool(config);
    }
}

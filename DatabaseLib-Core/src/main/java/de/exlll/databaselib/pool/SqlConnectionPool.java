package de.exlll.databaselib.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlConnectionPool {
    void stop();

    /**
     * Returns a {@code Connection} from the pool.
     * <p>
     * The connection must manually be closed after usage.
     *
     * @return {@code Connection} from pool
     * @throws SQLException if a database access error occurs
     */
    Connection getConnection() throws SQLException;
}

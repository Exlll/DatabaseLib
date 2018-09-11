package de.exlll.databaselib.sql;

import de.exlll.databaselib.sql.pool.SqlConnectionPool;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

public class DummySqlConnectionPool implements SqlConnectionPool {
    private final AtomicInteger connectionCallCount = new AtomicInteger();
    private final DummyConnection dummyConnection = new DummyConnection();

    @Override
    public void close() {
        dummyConnection.close();
    }

    @Override
    public Connection getConnection() {
        connectionCallCount.incrementAndGet();
        return dummyConnection;
    }

    public int getConnectionCallCount() {
        return connectionCallCount.get();
    }
}

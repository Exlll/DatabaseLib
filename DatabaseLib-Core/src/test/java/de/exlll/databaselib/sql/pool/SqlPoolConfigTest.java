package de.exlll.databaselib.sql.pool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlPoolConfigTest {
    private SqlPoolConfig.Builder builder;

    @BeforeEach
    void setUp() {
        builder = SqlPoolConfig.builder();
    }

    @Test
    void setProtocolRequiresValidProtocol() {
        builder.setProtocol("mysql")
                .setProtocol("mariadb")
                .setProtocol("postgresql");
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.setProtocol("h2")
        );
    }

    @Test
    void setPortRequiresValidPort() {
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.setPort(-1)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.setPort(0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.setPort(65536)
        );
        builder.setPort(123).setPort(65535);
    }

    @Test
    void setPoolSizeRequiresValidPoolSize() {
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.setPoolSize(0, 0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.setPoolSize(10, 0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.setPoolSize(10, 9)
        );
        builder.setPoolSize(10, 10);
    }
}
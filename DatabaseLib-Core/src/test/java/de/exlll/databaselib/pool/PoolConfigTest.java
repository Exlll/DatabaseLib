package de.exlll.databaselib.pool;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class PoolConfigTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private PoolConfig.Builder builder;

    @Before
    public void setUp() throws Exception {
        this.builder = new PoolConfig.Builder();
    }

    @Test
    public void checkDefaultValues() throws Exception {
        PoolConfig cfg = PoolConfig.DEFAULT;
        assertThat(cfg.getUsername(), is("root"));
        assertThat(cfg.getPassword(), is(""));
        assertThat(cfg.getDatabase(), is("minecraft"));
        assertThat(cfg.getHost(), is("localhost"));
        assertThat(cfg.getPort(), is(3306));
        assertThat(cfg.getCorePoolSize(), is(1));
        assertThat(cfg.getMaximumPoolSize(), is(3));
        assertThat(cfg.getDriverProperties(), is(Collections.emptyMap()));
    }

    @Test
    public void poolPropertiesNotModifiable() throws Exception {
        Map<String, String> map = builder.build().getDriverProperties();
        exception.expect(UnsupportedOperationException.class);
        map.put("a", "b");
    }

    @Test
    public void builderSetUsername() throws Exception {
        builder.setUsername("A");
        assertThat(builder.build().getUsername(), is("A"));
    }

    @Test
    public void builderSetPassword() throws Exception {
        builder.setPassword("A");
        assertThat(builder.build().getPassword(), is("A"));
    }

    @Test
    public void builderSetDatabase() throws Exception {
        builder.setDatabase("A");
        assertThat(builder.build().getDatabase(), is("A"));
    }

    @Test
    public void builderSetHost() throws Exception {
        builder.setHost("A");
        assertThat(builder.build().getHost(), is("A"));
    }

    @Test
    public void builderSetPort() throws Exception {
        builder.setPort(42);
        assertThat(builder.build().getPort(), is(42));
    }

    @Test
    public void builderSetPoolSize() throws Exception {
        builder.setPoolSize(42, 43);
        assertThat(builder.build().getCorePoolSize(), is(42));
        assertThat(builder.build().getMaximumPoolSize(), is(43));
    }

    @Test
    public void builderSetProperties() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("A", "B");
        builder.setDriverProperties(map);
        assertThat(builder.build().getDriverProperties(), is(map));
    }

    @Test
    public void builderAddProperty() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("A", "B");
        builder.addDriverProperty("A", "B");
        assertThat(builder.build().getDriverProperties(), is(map));
        builder.addDriverProperty("B", "B");
        assertThat(builder.build().getDriverProperties(), not(map));
    }

    @Test
    public void builderChecksValidPort() throws Exception {
        int i = 0;
        try {
            builder.setPort(0);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Problem: The port 0 is not valid.\n" +
                    "Solution: Use a port between 1 and 65535."));
            i++;
        }
        try {
            builder.setPort(65536);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Problem: The port 65536 is not valid.\n" +
                    "Solution: Use a port between 1 and 65535."));
            i++;
        }
        builder.setPort(1);
        builder.setPort(65535);
        assertThat(i, is(2));
    }

    @Test
    public void builderChecksValidPoolSize() throws Exception {
        int i = 0;

        try {
            builder.setPoolSize(0, 1);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Problem: The core pool size is 0 (less than 1).\n" +
                    "Solution: Pass a core pool size greater than or equal to 1.")
            );
            i++;
        }
        try {
            builder.setPoolSize(1, 0);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Problem: The maximum pool size is 0 (less than 1).\n" +
                    "Solution: Pass a maximum pool size greater than or equal to 1.")
            );
            i++;
        }
        try {
            builder.setPoolSize(2, 1);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Problem: The maximum pool size 1 is less than the core " +
                    "pool size 2.\nSolution: Make the maximum pool size greater than or equal " +
                    "to the core pool size.")
            );
            i++;
        }

        builder.setPoolSize(1, 1);
        builder.setPoolSize(1, 2);
        assertThat(i, is(3));
    }

    @Test
    public void builderRequiresNonNullUsername() throws Exception {
        exception.expect(NullPointerException.class);
        builder.setUsername(null);
    }

    @Test
    public void builderRequiresNonNullPassword() throws Exception {
        exception.expect(NullPointerException.class);
        builder.setPassword(null);
    }

    @Test
    public void builderRequiresNonNullDatabase() throws Exception {
        exception.expect(NullPointerException.class);
        builder.setDatabase(null);
    }

    @Test
    public void builderRequiresNonNullHost() throws Exception {
        exception.expect(NullPointerException.class);
        builder.setHost(null);
    }

    @Test
    public void builderRequiresNonNullProperties() throws Exception {
        exception.expect(NullPointerException.class);
        builder.setDriverProperties(null);
    }

    @Test
    public void builderRequiresNonNullProperty() throws Exception {
        exception.expect(NullPointerException.class);
        builder.addDriverProperty(null, "");
    }

    @Test
    public void builderRequiresNonNullPropertyValue() throws Exception {
        exception.expect(NullPointerException.class);
        builder.addDriverProperty("", null);
    }
}
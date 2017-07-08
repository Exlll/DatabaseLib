package de.exlll.databaselib.pool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PoolConfig {
    public static final PoolConfig DEFAULT = new Builder().build();
    private final String username;
    private final String password;
    private final String database;
    private final String host;
    private final int port;
    private final int maximumPoolSize;
    private final int corePoolSize;
    private final Map<String, String> driverProperties;

    private PoolConfig(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.database = builder.database;
        this.host = builder.host;
        this.port = builder.port;
        this.maximumPoolSize = builder.maximumPoolSize;
        this.corePoolSize = builder.corePoolSize;
        this.driverProperties = Collections.unmodifiableMap(builder.properties);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * @return unmodifiable Map
     */
    public Map<String, String> getDriverProperties() {
        return driverProperties;
    }

    public static final class Builder {
        private String username = "root";
        private String password = "";
        private String database = "minecraft";
        private String host = "localhost";
        private int port = 3306;
        private int maximumPoolSize = 3;
        private int corePoolSize = 1;
        private Map<String, String> properties = new HashMap<>();

        /**
         * Sets the username that is used to connect to the database.
         *
         * @param username username used to connect to the database
         * @return this {@code Builder}
         * @throws NullPointerException if {@code username} is null
         */
        public Builder setUsername(String username) {
            Objects.requireNonNull(username);
            this.username = username;
            return this;
        }

        /**
         * Sets the password that is used to connect to the database.
         *
         * @param password password used to connect to the database
         * @return this {@code Builder}
         * @throws NullPointerException if {@code password} is null
         */
        public Builder setPassword(String password) {
            Objects.requireNonNull(password);
            this.password = password;
            return this;
        }

        /**
         * Sets the database.
         *
         * @param database name of the database
         * @return this {@code Builder}
         * @throws NullPointerException if {@code database} is null
         */
        public Builder setDatabase(String database) {
            Objects.requireNonNull(database);
            this.database = database;
            return this;
        }

        /**
         * Sets the host of the database.
         *
         * @param host host of the database
         * @return this {@code Builder}
         * @throws NullPointerException if {@code host} is null
         */
        public Builder setHost(String host) {
            Objects.requireNonNull(host);
            this.host = host;
            return this;
        }

        /**
         * Sets the port of the database.
         *
         * @param port port of the database
         * @return this {@code Builder}
         * @throws IllegalArgumentException if port is invalid
         */
        public Builder setPort(int port) {
            if (port <= 0 || port > 65535) {
                String msg = "Problem: The port " + port + " is not valid.\n" +
                        "Solution: Use a port between 1 and 65535.";
                throw new IllegalArgumentException(msg);
            }
            this.port = port;
            return this;
        }

        /**
         * Sets the core and maximum pool size. The core pool size defines the
         * number of connections to keep in the pool, even if they are idle.
         *
         * @param corePoolSize    number of connections to keep in the pool
         * @param maximumPoolSize maximum number of connections
         * @return this {@code Builder}
         */
        public Builder setPoolSize(int corePoolSize, int maximumPoolSize) {
            if (corePoolSize < 1) {
                String msg = "Problem: The core pool size is " + corePoolSize +
                        " (less than 1).\nSolution: Pass a core pool size" +
                        " greater than or equal to 1.";
                throw new IllegalArgumentException(msg);
            }
            if (maximumPoolSize < 1) {
                String msg = "Problem: The maximum pool size is " + maximumPoolSize +
                        " (less than 1).\nSolution: Pass a maximum pool size" +
                        " greater than or equal to 1.";
                throw new IllegalArgumentException(msg);
            }
            if (maximumPoolSize < corePoolSize) {
                String msg = "Problem: The maximum pool size " + maximumPoolSize +
                        " is less than the core pool size " + corePoolSize + ".\n" +
                        "Solution: Make the maximum pool size greater than or equal " +
                        "to the core pool size.";
                throw new IllegalArgumentException(msg);
            }
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        /**
         * Sets the driver configuration properties.
         * <p>
         * <a href="https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html">
         * MySQL driver driverProperties</a>
         *
         * @param properties map containing driver properties
         * @return this {@code Builder}
         * @throws NullPointerException if {@code driverProperties} is null
         */
        public Builder setDriverProperties(Map<String, String> properties) {
            Objects.requireNonNull(properties);
            this.properties = properties;
            return this;
        }

        /**
         * Adds a driver configuration property.
         * <p>
         * <a href="https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html">
         * MySQL driver driverProperties</a>
         *
         * @param property driver property
         * @param value    property value
         * @return this {@code Builder}
         * @throws NullPointerException if {@code property} or {@code value} is null
         */
        public Builder addDriverProperty(String property, String value) {
            Objects.requireNonNull(property);
            Objects.requireNonNull(value);
            this.properties.put(property, value);
            return this;
        }

        /**
         * @return new {@code PoolConfig}
         */
        public PoolConfig build() {
            return new PoolConfig(this);
        }
    }
}

package de.exlll.databaselib.sql.submit;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

class SqlTaskTest {
    @Test
    void constructorRequiresNonNullFunction() {
        assertThrows(
                NullPointerException.class,
                () -> new SqlTaskImpl(null, (s, e) -> {})
        );
        SqlTaskImpl sqlTask = new SqlTaskImpl(s -> "", (s, e) -> {});
        sqlTask.execute(null);
    }

    @Test
    void constructorRequiresNonNullCallback() {
        assertThrows(
                NullPointerException.class,
                () -> new SqlTaskImpl(s -> "", null)
        );
    }

    private static final class SqlTaskImpl extends SqlTask<String, String> {
        public SqlTaskImpl(
                CheckedSqlFunction<? super String, ? extends String> function,
                BiConsumer<? super String, ? super Throwable> callback
        ) { super(function, callback); }

        @Override
        public void execute(Connection connection) {}
    }
}
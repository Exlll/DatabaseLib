package de.exlll.databaselib.sql.submit;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CheckedSqlFunctionTest {

    @Test
    void fromRequiresNonNullConsumer() {
        assertThrows(
                NullPointerException.class,
                () -> CheckedSqlFunction.from(null)
        );
    }

    @Test
    void fromReturnsConsumer() throws SQLException {
        CheckedSqlConsumer<AtomicInteger> consumer =
                AtomicInteger::incrementAndGet;

        CheckedSqlFunction<AtomicInteger, Void> function =
                CheckedSqlFunction.from(consumer);

        AtomicInteger integer = new AtomicInteger();
        function.apply(integer);
        assertThat(integer.get(), is(1));
    }
}
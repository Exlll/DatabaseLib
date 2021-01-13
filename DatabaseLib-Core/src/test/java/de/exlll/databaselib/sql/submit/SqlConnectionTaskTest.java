package de.exlll.databaselib.sql.submit;

import de.exlll.databaselib.sql.DummyConnection;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SqlConnectionTaskTest {

    @Test
    void executeAppliesFunction() {
        AtomicInteger integer = new AtomicInteger();
        SqlConnectionTask<String> task = new SqlConnectionTask<>(
                connection -> {
                    integer.incrementAndGet();
                    return "Hello";
                },
                (s, throwable) -> {}
        );
        task.execute(new DummyConnection());
        assertThat(integer.get(), is(1));
    }

    @Test
    void executeCallsCallbackOnSuccess() {
        AtomicInteger integer = new AtomicInteger();
        SqlConnectionTask<String> task = new SqlConnectionTask<>(
                connection -> "Hello",
                (s, throwable) -> {
                    integer.incrementAndGet();
                    assertThat(s, is("Hello"));
                    assertThat(throwable, nullValue());
                }
        );
        task.execute(new DummyConnection());
        assertThat(integer.get(), is(1));
    }

    @Test
    void executeCallsCallbackOnFailure() {
        AtomicInteger integer = new AtomicInteger();
        SqlConnectionTask<String> task = new SqlConnectionTask<>(
                connection -> {throw new RuntimeException("Hello");},
                (s, throwable) -> {
                    integer.incrementAndGet();
                    assertThat(throwable.getMessage(), is("Hello"));
                    assertThat(s, nullValue());
                }
        );
        task.execute(new DummyConnection());
        assertThat(integer.get(), is(1));

        task = new SqlConnectionTask<>(
                connection -> {throw new Error("World");},
                (s, throwable) -> {
                    integer.incrementAndGet();
                    assertThat(throwable.getMessage(), is("World"));
                    assertThat(s, nullValue());
                }
        );
        task.execute(new DummyConnection());
        assertThat(integer.get(), is(2));
    }
}
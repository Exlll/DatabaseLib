package de.exlll.databaselib.sql.submit;

import de.exlll.databaselib.sql.DummyConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

class AbstractSqlStatementTaskTest {
    @Test
    void executeAppliesFunction() {
        AtomicInteger integer = new AtomicInteger();
        AbstractSqlStatementTaskImpl task = new AbstractSqlStatementTaskImpl(
                statement -> {
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
        AbstractSqlStatementTaskImpl task = new AbstractSqlStatementTaskImpl(
                connection -> "Hello",
                (s, e) -> {
                    integer.incrementAndGet();
                    assertThat(s, is("Hello"));
                    assertThat(e, nullValue());
                }
        );
        task.execute(new DummyConnection());
        assertThat(integer.get(), is(1));
    }

    @Test
    void executeCallsCallbackOnFailure() {
        AtomicInteger integer = new AtomicInteger();
        AbstractSqlStatementTaskImpl task = new AbstractSqlStatementTaskImpl(
                connection -> {throw new RuntimeException("Hello");},
                (s, e) -> {
                    integer.incrementAndGet();
                    assertThat(e.getMessage(), is("Hello"));
                    assertThat(s, nullValue());
                }
        );
        task.execute(new DummyConnection());
        assertThat(integer.get(), is(1));

        task = new AbstractSqlStatementTaskImpl(
                connection -> {throw new Error("World");},
                (s, e) -> {
                    integer.incrementAndGet();
                    assertThat(e.getMessage(), is("World"));
                    assertThat(s, nullValue());
                }
        );
        task.execute(new DummyConnection());
        assertThat(integer.get(), is(2));
    }

    private static final class AbstractSqlStatementTaskImpl
            extends AbstractSqlStatementTask<Statement, String> {
        AbstractSqlStatementTaskImpl(
                CheckedSqlFunction<? super Statement, ? extends String> function,
                BiConsumer<? super String, ? super Throwable> callback
        ) {
            super(function, callback);
        }

        @Override
        CheckedSqlFunction<? super Connection, ? extends Statement> statementFactory() {
            return Connection::createStatement;
        }
    }
}
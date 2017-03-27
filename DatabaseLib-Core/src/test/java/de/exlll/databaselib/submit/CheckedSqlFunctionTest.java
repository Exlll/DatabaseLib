package de.exlll.databaselib.submit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class CheckedSqlFunctionTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void fromRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        CheckedSqlFunction.from(null);
    }

    @Test
    public void fromFunctionConsumesArgument() throws Exception {
        AtomicInteger integer = new AtomicInteger();
        CheckedSqlFunction<Integer, Void> func = CheckedSqlFunction.from(integer::addAndGet);

        assertThat(integer.get(), is(0));
        assertThat(func.apply(10), nullValue());
        assertThat(integer.get(), is(10));
    }
}
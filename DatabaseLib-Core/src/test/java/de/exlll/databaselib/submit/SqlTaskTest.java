package de.exlll.databaselib.submit;

import org.junit.Test;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class SqlTaskTest {
    public static final String HELLO_WORLD = "hello world";
    @Test
    public void finishCallsCallback() throws Exception {
        @SuppressWarnings("ThrowableNotThrown")
        Exception e = new Exception("");

        AtomicBoolean executeCalled = new AtomicBoolean();
        AtomicBoolean callbackCalled = new AtomicBoolean();

        SqlTask<String, String> task = new SqlTask<String, String>(null, (s, exception) -> {
            assertThat(s, is("hello world"));
            assertThat(exception, sameInstance(e));
            callbackCalled.set(true);
        }) {
            @Override
            public void execute(Connection connection) {
                this.exception = e;
                this.result = "hello world";
                executeCalled.set(true);
            }
        };

        assertThat(executeCalled.get(), is(false));
        assertThat(callbackCalled.get(), is(false));
        task.execute(null);
        task.finish();
        assertThat(executeCalled.get(), is(true));
        assertThat(callbackCalled.get(), is(true));
    }
}
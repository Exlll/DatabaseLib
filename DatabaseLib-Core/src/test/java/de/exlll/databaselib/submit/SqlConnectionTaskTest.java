package de.exlll.databaselib.submit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlConnectionTaskTest {
    @Test
    public void executeAppliesConnection() throws Exception {
        SqlConnectionTask<String> task = new SqlConnectionTask<>(
                connection -> SqlTaskTest.HELLO_WORLD, null
        );
        task.execute(null);

        assertThat(task.result, is(SqlTaskTest.HELLO_WORLD));
        assertThat(task.exception, nullValue());
    }

    @Test
    public void executeSetsExceptionOnException() throws Exception {
        SqlConnectionTask<String> task = new SqlConnectionTask<>(
                connection -> {
                    throw new RuntimeException(SqlTaskTest.HELLO_WORLD);
                }, null
        );
        task.execute(null);
        assertThat(task.exception, instanceOf(RuntimeException.class));
        assertThat(task.exception.getMessage(), is(SqlTaskTest.HELLO_WORLD));
    }
}
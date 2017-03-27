package de.exlll.databaselib.submit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class SqlTaskSubmitterTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    CheckedSqlConsumer<Connection> connectionCheckedSqlConsumer = System.out::println;
    CheckedSqlConsumer<Statement> statementCheckedSqlConsumer = System.out::println;
    CheckedSqlConsumer<PreparedStatement> preparedStatementCheckedSqlConsumer = System.out::println;
    CheckedSqlConsumer<CallableStatement> callableStatementCheckedSqlConsumer = System.out::println;
    CheckedSqlFunction<Connection, Object> connectionCheckedSqlFunction = o -> new Object();
    CheckedSqlFunction<Statement, Object> statementCheckedSqlFunction = o -> new Object();
    CheckedSqlFunction<PreparedStatement, Object> preparedStatementCheckedSqlFunction = o -> new Object();
    CheckedSqlFunction<CallableStatement, Object> callableStatementCheckedSqlFunction = o -> new Object();
    Consumer<Exception> consumer = System.out::println;
    BiConsumer<Object, Exception> biConsumer = (o, exception) -> System.out.println(o);
    SqlTaskSubmitter submitter = new SqlTaskSubmitter() {
        @Override
        protected void submit(SqlTask<?, ?> task) {
        }

        @Override
        protected Connection getConnection() throws SQLException {
            return null;
        }
    };

    @Test
    public void toBiConsumerConvertsConsumer() throws Exception {
        AtomicReference<Exception> reference = new AtomicReference<>();
        Consumer<Exception> consumer = reference::set;

        BiConsumer<?, Exception> biConsumer = SqlTaskSubmitter.toBiConsumer(consumer);

        @SuppressWarnings("ThrowableNotThrown")
        Exception e = new Exception("");

        biConsumer.accept(null, e);
        assertThat(reference.get(), sameInstance(e));
    }

    @Test
    public void submitConnectionTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitConnectionTask(null, consumer);
    }

    @Test
    public void submitConnectionTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitConnectionTask(connectionCheckedSqlConsumer, null);
    }

    @Test
    public void submitConnectionTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitConnectionTask(null, biConsumer);
    }

    @Test
    public void submitConnectionTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitConnectionTask(connectionCheckedSqlFunction, null);
    }

    @Test
    public void newConnectionTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newConnectionTask(null, consumer);
    }

    @Test
    public void newConnectionTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newConnectionTask(connectionCheckedSqlConsumer, null);
    }

    @Test
    public void newConnectionTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newConnectionTask(null, biConsumer);
    }

    @Test
    public void newConnectionTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newConnectionTask(connectionCheckedSqlFunction, null);
    }

    @Test
    public void submitStatementTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitStatementTask(null, consumer);
    }

    @Test
    public void submitStatementTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitStatementTask(statementCheckedSqlConsumer, null);
    }

    @Test
    public void submitStatementTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitStatementTask(null, biConsumer);
    }

    @Test
    public void submitStatementTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitStatementTask(statementCheckedSqlFunction, null);
    }

    @Test
    public void newStatementTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newStatementTask(null, consumer);
    }

    @Test
    public void newStatementTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newStatementTask(statementCheckedSqlConsumer, null);
    }

    @Test
    public void newStatementTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newStatementTask(null, biConsumer);
    }

    @Test
    public void newStatementTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newStatementTask(statementCheckedSqlFunction, null);
    }

    @Test
    public void submitPreparedStatementTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitPreparedStatementTask("", null, consumer);
    }

    @Test
    public void submitPreparedStatementTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitPreparedStatementTask("", preparedStatementCheckedSqlConsumer, null);
    }

    @Test
    public void submitPreparedStatementTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitPreparedStatementTask("", null, biConsumer);
    }

    @Test
    public void submitPreparedStatementTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitPreparedStatementTask("", preparedStatementCheckedSqlFunction, null);
    }

    @Test
    public void submitPreparedStatementTaskRequiresNonNullQuery() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitPreparedStatementTask(null, preparedStatementCheckedSqlConsumer, consumer);
    }

    @Test
    public void submitPreparedStatementTaskRequiresNonNullQuery2() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitPreparedStatementTask(null, preparedStatementCheckedSqlFunction, biConsumer);
    }

    @Test
    public void newPreparedStatementTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newPreparedStatementTask("", null, consumer);
    }

    @Test
    public void newPreparedStatementTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newPreparedStatementTask("", preparedStatementCheckedSqlConsumer, null);
    }

    @Test
    public void newPreparedStatementTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newPreparedStatementTask("", null, biConsumer);
    }

    @Test
    public void newPreparedStatementTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newPreparedStatementTask("", preparedStatementCheckedSqlFunction, null);
    }

    @Test
    public void newPreparedStatementTaskRequiresNonNullQuery() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newPreparedStatementTask(null, preparedStatementCheckedSqlConsumer, consumer);
    }

    @Test
    public void newPreparedStatementTaskRequiresNonNullQuery2() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newPreparedStatementTask(null, preparedStatementCheckedSqlFunction, biConsumer);
    }

    @Test
    public void submitCallableStatementTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitCallableStatementTask("", null, consumer);
    }

    @Test
    public void submitCallableStatementTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitCallableStatementTask("", callableStatementCheckedSqlConsumer, null);
    }

    @Test
    public void submitCallableStatementTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitCallableStatementTask("", null, biConsumer);
    }

    @Test
    public void submitCallableStatementTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitCallableStatementTask("", callableStatementCheckedSqlFunction, null);
    }

    @Test
    public void submitCallableStatementTaskRequiresNonNullQuery() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitCallableStatementTask(null, callableStatementCheckedSqlConsumer, consumer);
    }

    @Test
    public void submitCallableStatementTaskRequiresNonNullQuery2() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.submitCallableStatementTask(null, callableStatementCheckedSqlFunction, biConsumer);
    }

    @Test
    public void newCallableStatementTaskRequiresNonNullSqlConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newCallableStatementTask("", null, consumer);
    }

    @Test
    public void newCallableStatementTaskRequiresNonNullConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newCallableStatementTask("", callableStatementCheckedSqlConsumer, null);
    }

    @Test
    public void newCallableStatementTaskRequiresNonNullSqlFunction() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newCallableStatementTask("", null, biConsumer);
    }

    @Test
    public void newCallableStatementTaskRequiresNonNullBiConsumer() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newCallableStatementTask("", callableStatementCheckedSqlFunction, null);
    }

    @Test
    public void newCallableStatementTaskRequiresNonNullQuery() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newCallableStatementTask(null, callableStatementCheckedSqlConsumer, consumer);
    }

    @Test
    public void newCallableStatementTaskRequiresNonNullQuery2() throws Exception {
        exception.expect(NullPointerException.class);
        submitter.newCallableStatementTask(null, callableStatementCheckedSqlFunction, biConsumer);
    }
}
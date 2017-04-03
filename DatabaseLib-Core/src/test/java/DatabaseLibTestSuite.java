import de.exlll.databaselib.submit.*;
import de.exlll.databaselib.submit.configure.PreparationStrategyTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CheckedSqlFunctionTest.class,
        TaskPriorityTest.class,
        SqlTaskTest.class,
        SqlStatementTaskTest.class,
        SqlPreparedStatementTaskTest.class,
        SqlCallableStatementTaskTest.class,
        SqlConnectionTaskTest.class,
        SqlTaskSubmitterTest.class,
        PreparationStrategyTest.class
})
public class DatabaseLibTestSuite {
}

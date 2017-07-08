import de.exlll.databaselib.pool.PoolConfigTest;
import de.exlll.databaselib.submit.*;
import de.exlll.databaselib.submit.configure.PreparationStrategyTest;
import de.exlll.databaselib.util.ScriptRunnerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CheckedSqlFunctionTest.class,
        PoolConfigTest.class,
        SqlTaskTest.class,
        SqlStatementTaskTest.class,
        SqlPreparedStatementTaskTest.class,
        SqlCallableStatementTaskTest.class,
        SqlConnectionTaskTest.class,
        SqlTaskSubmitterTest.class,
        PreparationStrategyTest.class,
        ScriptRunnerTest.class
})
public class DatabaseLibTestSuite {
}

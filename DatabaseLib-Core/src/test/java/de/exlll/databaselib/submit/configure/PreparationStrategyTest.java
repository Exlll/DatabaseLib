package de.exlll.databaselib.submit.configure;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PreparationStrategyTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void withColumnIndexesRequiresAtLeastOneIndex() throws Exception {
        exception.expectMessage("no column indexes provided");
        exception.expect(IllegalArgumentException.class);
        PreparationStrategy.withColumnIndexes();
    }

    @Test
    public void withColumnNamesRequiresAtLeastOneName() throws Exception {
        exception.expectMessage("no column names provided");
        exception.expect(IllegalArgumentException.class);
        PreparationStrategy.withColumnNames();
    }
}
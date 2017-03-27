package de.exlll.databaselib.submit;

import org.junit.Test;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class TaskPriorityTest {
    @Test
    public void lowLowerThanNormal() throws Exception {
        assertThat(TaskPriority.LOW.intValue(), lessThan(TaskPriority.NORMAL.intValue()));
    }

    @Test
    public void normalLowerThanHigh() throws Exception {
        assertThat(TaskPriority.NORMAL.intValue(), lessThan(TaskPriority.HIGH.intValue()));
    }
}
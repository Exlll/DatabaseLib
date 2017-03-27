package de.exlll.databaselib.submit;

public enum TaskPriority {
    LOW(10),
    NORMAL(20),
    HIGH(30);
    private final int priority;

    TaskPriority(int priority) {
        this.priority = priority;
    }

    public int intValue() {
        return priority;
    }
}

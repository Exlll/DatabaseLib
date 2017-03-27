package de.exlll.databaselib;


import de.exlll.asynclib.service.ServiceConfig;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

import java.nio.file.Path;

class AsyncConfig extends Configuration {
    private static final ServiceConfig DEFAULT = ServiceConfig.DEFAULT_CONFIG;
    private transient ServiceConfig config = DEFAULT;
    @Comment("number of threads that are started for task execution")
    private int numThreads = DEFAULT.getNumThreads();
    @Comment("poll period in ticks")
    private int pollPeriod = DEFAULT.getPollPeriod();
    @Comment({
            "maximum number of tasks that are finished per run",
            "a negative number or zero disable this check"
    })
    private int maxTasksPerRun = DEFAULT.getMaxTasksPerRun();
    @Comment({
            "maximum number of milliseconds a run lasts",
            "a negative number or zero disable this check"
    })
    private long maxMilliSecondsPerRun = DEFAULT.getMaxMillisecondsPerRun();

    public AsyncConfig(Path configPath) {
        super(configPath);
    }

    @Override
    protected void postLoadHook() {
        config = new ServiceConfig.Builder()
                .setMaxMillisecondsPerRun(maxMilliSecondsPerRun)
                .setMaxTasksPerRun(maxTasksPerRun)
                .setNumThreads(numThreads)
                .setPollPeriod(pollPeriod)
                .build();
    }

    public ServiceConfig getConfig() {
        return config;
    }
}

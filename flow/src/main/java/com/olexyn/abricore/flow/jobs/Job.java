package com.olexyn.abricore.flow.jobs;

import com.olexyn.abricore.flow.JobTimer;
import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.abricore.util.log.LogU;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;

import static com.olexyn.abricore.flow.JobType.UNKNOWN;
import static com.olexyn.abricore.util.enums.FlowHint.OK;


public class Job extends CtxAware implements Runnable {

    @Getter
    private Thread thread = null;

    protected final String jobName;

    @Getter
    @Setter
    private boolean cancelled = false;

    public int loopCount = 0;
    public String sleepMilliPropertyKey;

    @Getter
    private final JobTimer jobTimer = new JobTimer();

    protected Job(ConfigurableApplicationContext ctx) {
        super(ctx);
        this.jobName = Thread.currentThread().getName();
    }

    public Job start() {
        thread.start();
        return this;
    }

    @Override
    public void run() { /* NOP */ }

    public FlowHint fetchData() { return OK; }

    protected final void logFetchStart() {
        LogU.infoStart("fetch data");
    }

    protected final void logFetchEnd() {
        LogU.infoEnd("fetch data");
    }

    public JobType getType() { return UNKNOWN; }

    /**
     * Keep this package private.
     */
    void setThread(Thread thread) {
        this.thread = thread;
    }

}
package com.olexyn.abricore.flow.jobs;

import com.olexyn.abricore.flow.JobTimer;
import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.abricore.util.log.LogU;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

import static com.olexyn.abricore.flow.JobType.UNKNOWN;
import static com.olexyn.abricore.util.Constants.EMPTY;
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
    public final void run() {
        preRun();
        nestedRun();
        postRun();
    }

    private void preRun() {
        bean(JobKeeper.class).addJob(this);
        LogU.infoStart(getUuid().toString());
    }

    protected void nestedRun() { /* NOP */ }

    private void postRun() {
        LogU.infoEnd(getUuid().toString());
        bean(JobKeeper.class).removeJob(this);
    }


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

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getType(), jobName);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Job otherJob
            && otherJob.getUuid().equals(getUuid())
            && otherJob.getType() == getType()
            && otherJob.jobName.equals(jobName);
    }

}

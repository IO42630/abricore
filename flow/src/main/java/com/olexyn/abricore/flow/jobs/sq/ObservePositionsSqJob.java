package com.olexyn.abricore.flow.jobs.sq;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.Job;
import com.olexyn.abricore.navi.sq.SqNavigator;
import com.olexyn.abricore.store.runtime.PositionService;
import com.olexyn.abricore.store.runtime.TradeService;
import com.olexyn.abricore.util.enums.FlowHint;
import org.springframework.context.ConfigurableApplicationContext;

import static com.olexyn.abricore.util.enums.FlowHint.OK;


/**
 * This job has the simple task of fetching: <br>
 * - active trades <br>
 * - cash <br>
 * - active assets <br>
 * The goal here is to provide a sanity check before placing orders. <br>
 * As with most jobs, it fetches data from a Navigator and saves it to a Service. <br>
 * Thus while the job runs independently, it is synchronized on Navigator and Service. <br>
 */
public class ObservePositionsSqJob extends Job {

    private final SqNavigator nav;

    public ObservePositionsSqJob(ConfigurableApplicationContext ctx) {
        super(ctx);
        nav = ctx.getBean(SqNavigator.class);
        this.sleepMilliPropertyKey = "observe.positions.sq.interval.milli";
    }

    @Override
    public void nestedRun() {
        nav.doLogin();
        while (!isCancelled()) {
            fetchData();
            loopCount++;
            getJobTimer().sleepMilli(this, sleepMilliPropertyKey);
        }
    }

    /**
     * Fetch data from SQ. Save data to Service
     */
    @Override
    public FlowHint fetchData() {
        logFetchStart();
        bean(TradeService.class).update(nav.fetchTrades());
        bean(PositionService.class).update(nav.fetchPositions());
        logFetchEnd();
        return OK;
    }

    @Override
    public JobType getType() { return JobType.OBS_POS_SQ; }

}

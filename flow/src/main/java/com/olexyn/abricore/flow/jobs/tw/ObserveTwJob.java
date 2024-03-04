package com.olexyn.abricore.flow.jobs.tw;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.SJob;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.navi.Session;
import com.olexyn.abricore.navi.tw.TwNavigator;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.enums.FlowHint;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Set;

import static com.olexyn.abricore.util.enums.FlowHint.OK;

public class ObserveTwJob extends SJob {

    protected List<AssetDto> assetsToObserve;
    private final TwNavigator nav;

    public ObserveTwJob(ConfigurableApplicationContext ctx, StrategyDto strategy, List<AssetDto> assetsToObserve) {
        super(ctx, strategy);
        this.nav = bean(TwNavigator.class);
        this.assetsToObserve = assetsToObserve;
        this.sleepMilliPropertyKey = "tw.update.interval.milli";
    }

    @Override
    public void nestedRun() {
        nav.doLogin();
        while (!isCancelled()) {
            fetchData();
            getJobTimer().sleepMilli(this, sleepMilliPropertyKey);
        }
        synchronized(getLock()) {
            getLock().notifyAll();
        }
    }

    @Override
    public FlowHint fetchData() {
        Set<SnapshotDto> snapshots;
        synchronized(Session.class) {
            snapshots = nav.fetchQuotes(assetsToObserve);
        }
        bean(SeriesService.class).putData(snapshots);
        logFetchEnd();
        return OK;
    }

    @Override
    public JobType getType() { return JobType.OBS_TW; }

}

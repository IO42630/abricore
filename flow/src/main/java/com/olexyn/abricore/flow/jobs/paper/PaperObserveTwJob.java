package com.olexyn.abricore.flow.jobs.paper;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.tw.ObserveTwJob;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.runtime.PaperSeriesService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.min.log.LogU;
import com.olexyn.propconf.PropConf;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.olexyn.abricore.util.Constants.RNG;
import static com.olexyn.abricore.util.enums.FlowHint.OK;
import static com.olexyn.abricore.util.enums.FlowHint.STOP;

public class PaperObserveTwJob extends ObserveTwJob {


    @Getter
    @Setter
    private Instant current = null;

    private static final Duration PAPER_GRID = PropConf.getDuration("evo.paper.grid.milli");



    public PaperObserveTwJob(ConfigurableApplicationContext ctx, StrategyDto strategy) {
        super(ctx, strategy, List.of(strategy.getUnderlying()));
    }

    /**
     * DON'T <br>
     * - put any logic for cancelling Observers here.
     */
    @Override
    public void nestedRun() {
        synchronized(getLock()) {
            getLock().safeWait(PropConf.getDuration("paper.trade.wait.for.trade.job.seconds"));
        }
        long fetchCount = 0;
        current = getStrategy().getFrom();
        var fetchState = OK;
        while (fetchState == OK && current.isBefore(getStrategy().getTo())) {
            fetchState = fetchData();
            fetchCount++;
            setCurrent(getNextGrid(getCurrent()));
        }
        LogU.infoPlain("Simulation complete with %s fetches.", fetchCount);
        synchronized(getLock()) {
            getLock().notifyAll();
        }
        getThread().interrupt();
    }

    @Override
    public FlowHint fetchData() {
        for (AssetDto asset : assetsToObserve) {
            var realSeries = bean(SeriesService.class).of(asset);
            var fakeSeries = bean(PaperSeriesService.class).of(asset);
            if (realSeries == null || fakeSeries == null) { return STOP; }
            try {
                var nextI = realSeries.getSameOrFirstAfter(current);
                if (nextI == null) { return STOP; }
                var realSnapshot = realSeries.getSnapshot(nextI);
                fakeSeries.put(realSnapshot);
            } catch (Exception e) { LogU.infoPlain(e.getMessage(), e); }
        }
        return OK;
    }

    @Override
    public JobType getType() {
        return JobType.PAPER_OBS_TW;
    }

    Instant getNextGrid(Instant current) {
        var step = PAPER_GRID
            .multipliedBy(RNG.nextInt(70, 80))
            .dividedBy(RNG.nextInt(60, 100));
        return current.plus(step);
    }

}

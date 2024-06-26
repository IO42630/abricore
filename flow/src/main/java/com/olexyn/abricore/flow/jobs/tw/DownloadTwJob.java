package com.olexyn.abricore.flow.jobs.tw;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.Job;
import com.olexyn.abricore.flow.jobs.store.ReadTmpCsvToDbJob;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.navi.tw.TwNavigator;
import com.olexyn.abricore.store.dao.EventDao;
import com.olexyn.abricore.util.enums.CmdOptions;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.abricore.util.enums.Interval;
import com.olexyn.propconf.PropConf;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.flow.jobs.JobStarter.startJob;
import static com.olexyn.abricore.util.enums.CmdOptions.FORCE;
import static com.olexyn.abricore.util.enums.FlowHint.OK;

public class DownloadTwJob extends Job {


    private static final long INTERVAL_BETWEEN_DOWNLOADS = PropConf.getLong("tw.download.interval.minutes");

    private final List<AssetDto> assets = new ArrayList<>();
    private final List<CmdOptions> options = new ArrayList<>();

    public DownloadTwJob(
        ConfigurableApplicationContext ctx,
        List<AssetDto> assets,
        List<CmdOptions> options
    ) {
        super(ctx);
        this.options.addAll(options);
        this.assets.addAll(assets);
        this.sleepMilliPropertyKey = "tw.download.core.loop.sleep.milli";
    }



    @Override
    public void nestedRun() {
        bean(TwNavigator.class).doLogin();
        setReady(true);
        while (!isCancelled()) {
            var lastTwDownload = bean(EventDao.class).getInstant("tw.last.download");
            if (options.contains(FORCE) || lastTwDownload.plus(Duration.ofMinutes(INTERVAL_BETWEEN_DOWNLOADS)).isBefore(Instant.now())) {
                options.remove(FORCE);
                fetchData();
                bean(EventDao.class).set("tw.last.download", Instant.now().toString());
                startJob(new ReadTmpCsvToDbJob(getCtx()));
            }
            getJobTimer().sleepMilli(this, sleepMilliPropertyKey);
        }
    }

    @Override
    public FlowHint fetchData() {
        logFetchStart();
        bean(TwNavigator.class).fetchHistoricalData(assets, List.of(Interval.S_1));
        logFetchEnd();
        return OK;
    }

    @Override
    public JobType getType() { return JobType.DL_TW; }

}

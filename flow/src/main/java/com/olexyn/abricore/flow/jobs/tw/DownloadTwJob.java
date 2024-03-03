package com.olexyn.abricore.flow.jobs.tw;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.Job;
import com.olexyn.abricore.flow.jobs.store.ReadTmpCsvToDbJob;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.navi.tw.TwNavigator;
import com.olexyn.abricore.util.Property;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.abricore.util.enums.Interval;
import com.olexyn.abricore.util.enums.CmdOptions;
import com.olexyn.abricore.util.log.LogU;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.flow.jobs.JobStarter.startJob;
import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.enums.FlowHint.OK;
import static com.olexyn.abricore.util.enums.CmdOptions.FORCE;

public class DownloadTwJob extends Job {


    private static final long INTERVAL_BETWEEN_DOWNLOADS = Long.parseLong(Property.get("tw.download.interval.minutes"));

    private final List<AssetDto> assets = new ArrayList<>();
    private final List<CmdOptions> options = new ArrayList<>();

    public DownloadTwJob(ConfigurableApplicationContext ctx, List<AssetDto> assets, List<CmdOptions> options) {
        super(ctx);
        this.options.addAll(options);
        this.assets.addAll(assets);
        this.sleepMilliPropertyKey = "tw.download.core.loop.sleep.milli";
    }



    @Override
    public void run() {
        LogU.infoStart(EMPTY);
        bean(TwNavigator.class).doLogin();
        while (!isCancelled()) {
            var lastTwDownload = Instant.parse(Property.get("tw.last.download"));
            if (options.contains(FORCE) || lastTwDownload.plus(Duration.ofMinutes(INTERVAL_BETWEEN_DOWNLOADS)).isBefore(Instant.now())) {
                options.remove(FORCE);
                fetchData();
                Property.getEvents().setProperty("tw.last.download", Instant.now().toString());
                startJob(new ReadTmpCsvToDbJob(getCtx()));
            }
            getJobTimer().sleepMilli(this, sleepMilliPropertyKey);
        }
        LogU.infoEnd(EMPTY);
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

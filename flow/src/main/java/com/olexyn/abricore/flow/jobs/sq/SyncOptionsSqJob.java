package com.olexyn.abricore.flow.jobs.sq;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.Job;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.navi.sq.SqNavigator;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.abricore.util.enums.OptionStatus;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.propconf.PropConf;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.util.stream.Stream;

import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.enums.FlowHint.OK;

/**
 * CDFs must be in range of the current price of the underlying Asset.
 * Thus fetch the CDFs and put them as Assets.
 */
public class SyncOptionsSqJob extends Job {

    private final StrategyDto strategy;
    private final AssetDto underlyingAsset;

    public SyncOptionsSqJob(ConfigurableApplicationContext ctx, StrategyDto strategy) {
        super(ctx);
        this.strategy = strategy;
        this.underlyingAsset = strategy.getUnderlying();
        this.sleepMilliPropertyKey = "sync.options.sq.interval.milli";
    }

    @Override
    public void nestedRun() {
        bean(SqNavigator.class).doLogin();
        while (!isCancelled()) {
            fetchData();
            loopCount++;
            getJobTimer().sleepMilli(this, sleepMilliPropertyKey);
        }
    }

    @Override
    public FlowHint fetchData() {
        logFetchStart();
        findOptions()
            .map(this::fillDetails)
            .filter(optionDto -> optionDto.getStatus() != OptionStatus.DEAD)
            .peek(bean(AssetService.class)::addAsset)
            .forEach(bean(SeriesService.class)::of);
        LogU.infoPlain("TOTAL %s Assets.", bean(AssetService.class).getNames().size());
        logFetchEnd();
        return OK;
    }

    private Stream<OptionDto> findOptions() {
        var lastSnap = bean(SeriesService.class).getLast(underlyingAsset);
        if (lastSnap == null) { return Stream.empty(); }
        var days = PropConf.getLong("sync.options.sq.option.distance.timeframe.days");
        var series = bean(SeriesService.class).of(underlyingAsset, lastSnap.getInstant(), Duration.ofDays(days));
        if (series == null) { return Stream.empty(); }
        long minOptionDistance = strategy.getMinOptionDistance().generate(series);
        long maxOptionDistance = strategy.getMaxOptionDistance().generate(series);
        var foundOptions = bean(SqNavigator.class).fetchOptions(
            strategy,
            bean(SeriesService.class).getLastTraded(underlyingAsset),
            minOptionDistance,
            maxOptionDistance
        );
        LogU.infoPlain("FOUND %s Options from Sq.", foundOptions.size());
        return foundOptions.stream();
    }

    private OptionDto fillDetails(OptionDto option) {
        var existing = bean(AssetService.class).ofName(option.getName());
        if (existing != null) {
            // with some option types, the strike changes, thus we need to update.
            // but no need for fetchAssetDetails since strike is part of basic data.
            return ((OptionDto) existing).mergeFrom(option);
        }
        return option.mergeFrom(bean(SqNavigator.class).fetchAssetDetails(option));
    }

    @Override
    public JobType getType() { return JobType.SYNC_OPTIONS_SQ; }

}

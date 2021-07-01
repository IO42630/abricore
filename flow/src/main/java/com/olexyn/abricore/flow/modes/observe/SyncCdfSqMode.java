package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.LogUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * CDFs must be in range of the current price of the underlying Asset.
 * Thus fetch the CDFs and put them as Assets.
 */
public class SyncCdfSqMode extends Mode {

    private static final Logger LOGGER = LogUtil.get(SyncCdfSqMode.class);

    private final Mission mission;

    public SyncCdfSqMode(Mission mission) {
        this.mission = mission;
    }

    private Set<Option> foundCdfs = new HashSet<>();

    @Override
    public void run() {
        SqSession.doLogin();
        timer.start();
        while (timer.hasNotPassedSeconds("run.time.seconds")) {
            try {
                fetchData();
                timer.sleepSeconds("cdf.update.interval.seconds");
            } catch (InterruptedException e) {
                LOGGER.warning(e.getMessage());
            }
        }
        try {
            AssetService.save();
            for (Asset cdf : foundCdfs) {
                SeriesService.save(SeriesService.of(cdf));
            }
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        SqSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException {
        Asset underlying = underlyingSeries.getAsset();
        synchronized (Session.class) {
            foundCdfs = SqNavigator.getCdf(
                underlying,
                mission.getStrategy().distanceGenerator.generate(underlying),
                mission.getStrategy().minRatio,
                mission.getStrategy().maxRatio
            );
            foundCdfs.forEach(AssetService::addAsset);
            foundCdfs.forEach(SeriesService::add);
        }
    }
}

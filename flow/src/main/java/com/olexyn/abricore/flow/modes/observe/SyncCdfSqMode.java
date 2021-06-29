package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.flow.MainApp;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

/**
 * CDFs must be in range of the current price of the underlying Asset.
 * Thus fetch the CDFs and put them as Assets.
 */
public class SyncCdfSqMode extends ObserveMode {

    private SqSession sqSession;
    private SqNavigator sqNavigator;

    public SyncCdfSqMode(Asset asset) {
        super(asset);
    }

    @Override
    public void run() {
        start();
        timer.start();
        while (!timer.hasPassed(Duration.ofSeconds(Long.parseLong(MainApp.config.getProperty("run.time"))))) {
            try {
                fetchData();
                Thread.sleep(Long.parseLong(MainApp.config.getProperty("cdf.update.interval")));
            } catch (InterruptedException | IOException ignored) {}
        }
        for (Series cdfSeries : cdfSeriesList) {
            SeriesService.save(cdfSeries);
        }
        stop();
    }

    @Override
    public void start() {
        sqSession = new SqSession();
        sqSession.doLogin();
        sqNavigator = new SqNavigator();
    }

    @Override
    public void stop() {
        sqSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException, IOException {
        Set<Asset> foundCdfs = sqNavigator.getCdf(underlyingSeries.getAsset(), OptionType.CALL, new ANum(23), new ANum(1), 1d, 1d);
        foundCdfs.forEach(AssetService::addAsset);
        foundCdfs.forEach(SeriesService::add);
        AssetService.save();
        int br = 0;
    }
}

package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.io.IOException;
import java.util.Set;

/**
 * CDFs must be in range of the current price of the underlying Asset.
 * Thus fetch the CDFs and put them as Assets.
 */
public class SyncCdfSqMode extends ObserveMode {

    public SyncCdfSqMode(Asset asset) {
        super(asset);
    }

    @Override
    public void run() {
        SqSession.doLogin();
        timer.start();
        while (!timer.hasPassedSeconds("run.time.seconds")) {
            try {
                fetchData();
                timer.sleepSeconds("cdf.update.interval.seconds");
            } catch (InterruptedException | IOException ignored) {}
        }
        for (Series cdfSeries : cdfSeriesList) {
            SeriesService.save(cdfSeries);
        }
        SqSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException, IOException {
        synchronized (Session.class) {
            Set<Asset> foundCdfs = SqNavigator.getCdf(underlyingSeries.getAsset(), OptionType.CALL, new ANum(23), new ANum(1), 1d, 1d);
            foundCdfs.forEach(AssetService::addAsset);
            foundCdfs.forEach(SeriesService::add);
            AssetService.save();
            int br = 0;
        }
    }
}

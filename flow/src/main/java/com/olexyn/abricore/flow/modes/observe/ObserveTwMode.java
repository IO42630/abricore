package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Duration;
import java.util.List;

public class ObserveTwMode extends ObserveMode {

    private TwSession twSession;
    private TwFetch twFetch;

    public ObserveTwMode(Asset asset) {
        super(asset);
    }

    public void run() throws InterruptedException {
        start();
        timer.start();
        while (timer.hasPassed(Duration.ofSeconds(10))) {
            fetchData();
            Thread.sleep(10L);
        }
        SnapShotSeries snapShotSeries = getCdfSeriesList().get(0);
        SnapSeriesService.save(snapShotSeries);
        stop();
    }

    @Override
    public void start() {
        twSession = new TwSession();
        twFetch = new TwFetch(twSession.doLogin());
    }

    @Override
    public void stop() {
        twSession.doLogout();
    }



    @Override
    public void fetchData() throws InterruptedException {
        List<AssetSnapshot> snapshots = twFetch.fetchQuotes(getAssets());

        for (AssetSnapshot snapshot : snapshots) {
            getSnapShotSeries(snapshot.getAsset())
                .ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
        }
    }

}

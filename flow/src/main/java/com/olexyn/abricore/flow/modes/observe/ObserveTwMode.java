package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.flow.MainApp;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;

import java.time.Duration;
import java.util.List;

public class ObserveTwMode extends ObserveMode {

    private TwSession twSession;
    private TwFetch twFetch;

    public ObserveTwMode(Asset asset) {
        super(asset);
    }

    public void run() {
        start();
        timer.start();
        while (!timer.hasPassed(Duration.ofSeconds(Long.parseLong(MainApp.config.getProperty("run.time.seconds"))))) {
            try {
                fetchData();
                Thread.sleep(Long.parseLong(MainApp.config.getProperty("tw.update.interval.milli")));
            } catch (InterruptedException ignored) {

            }
        }
        Series series = getCdfSeriesList().get(0);
        SeriesService.save(series);
        stop();
    }

    @Override
    public void start() {
        twSession = new TwSession();
        twSession.doLogin();
        twFetch = new TwFetch();
    }

    @Override
    public void stop() {
        twSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException {
        synchronized (Session.class) {
            List<AssetSnapshot> snapshots = twFetch.fetchQuotes(getAssets());
            SeriesService.putData(snapshots);
        }
    }

}

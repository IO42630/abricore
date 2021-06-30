package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.tw.TwNavigator;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;

import java.util.List;

public class ObserveTwMode extends ObserveMode {

    public ObserveTwMode(Asset asset) {
        super(asset);
    }

    public void run() {
        TwSession.doLogin();
        timer.start();
        while (timer.hasNotPassedSeconds("run.time.seconds")) {
            try {
                fetchData();
                timer.sleepMilli("tw.update.interval.milli");
            } catch (InterruptedException ignored) {

            }
        }
        Series series = getCdfSeriesList().get(0);
        SeriesService.save(series);
        TwSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException {
        synchronized (Session.class) {
            List<AssetSnapshot> snapshots = TwNavigator.fetchQuotes(getAssets());
            SeriesService.putData(snapshots);
        }
    }

}

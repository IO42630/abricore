package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.tw.TwNavigator;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ObserveTwMode extends Mode {

    public List<Asset> assetsToObserve = new ArrayList<>();

    public ObserveTwMode(List<Asset> assetsToObserve) {
        this.assetsToObserve = assetsToObserve;
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
        for (Asset asset : assetsToObserve) {
            synchronized (SeriesService.class) {
                SeriesService.save(SeriesService.of(asset));
            }
        }
        TwSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException {
        List<AssetSnapshot> snapshots;
        synchronized (Session.class) {
            snapshots = TwNavigator.fetchQuotes(assetsToObserve);
        }
        synchronized (SeriesService.class) {
            SeriesService.putData(snapshots);
        }
    }

}

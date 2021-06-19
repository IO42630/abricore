package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.List;
import java.util.Optional;

public class ObserveTwMode extends ObserveMode {

    private TwSession twSession;
    private TwFetch twFetch;

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
    public void updateQuote() throws InterruptedException {
        List<AssetSnapshot> snapshots = twFetch.fetchQuotes(getAssets());

        for (AssetSnapshot snapshot : snapshots) {
            getSnapShotSeries(snapshot.getAsset())
                .ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
        }
    }

}

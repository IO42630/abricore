package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwLogin;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.List;
import java.util.Optional;

public class ObserveTwMode extends ObserveMode {

    private TwLogin twLogin;
    private TwFetch twFetch;

    @Override
    public void init() {
        twLogin = new TwLogin();
        twFetch = new TwFetch(twLogin.doLogin());
    }



    @Override
    public void updateQuote() throws InterruptedException {
        List<AssetSnapshot> snapshots = twFetch.fetchQuotes(getAssets());

        for (AssetSnapshot snapshot : snapshots) {
            Optional<SnapShotSeries> snapShotSeries = getSnapShotSeriesList().stream()
                .filter(x -> x.getAsset().equals(snapshot.getAsset()))
                .findFirst();
            snapShotSeries.ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
        }
    }

}

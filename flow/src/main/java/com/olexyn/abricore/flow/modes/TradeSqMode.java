package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TradeSqMode extends TradeMode {

    private SqSession sqSession;
    private SqNavigator sqNavigator;

    public void start() {
        sqSession = new SqSession();
        sqNavigator = new SqNavigator(sqSession.doLogin());
    }

    @Override
    public void updateQuote() {

        List<AssetSnapshot> snapshots = new ArrayList<>();
        snapshots.add(sqNavigator.resolveQuote(mission.getUnderlyingAsset(), null));

        for (Asset cdf : mission.getDerivatives()) {
            snapshots.add(sqNavigator.resolveQuote(cdf, null));
        }

        for (AssetSnapshot snapshot : snapshots) {
            getSnapShotSeries(snapshot.getAsset())
                .ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
        }

    }

    @Override
    public void stop() {
        sqSession.doLogout();
    }

}

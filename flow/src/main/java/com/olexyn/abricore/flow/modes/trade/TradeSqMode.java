package com.olexyn.abricore.flow.modes.trade;

import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TradeSqMode extends TradeMode {

    private SqSession sqSession;
    private SqNavigator sqNavigator;


    public TradeSqMode(Asset asset, Mission mission) {
        super(asset, mission);
    }



    public void start() {
        sqSession = new SqSession();
        sqNavigator = new SqNavigator(sqSession.doLogin());
    }


    /**
     * Fetch CDF data from SQ.
     */
    @Override
    public void fetchData() {

        List<AssetSnapshot> snapshots = new ArrayList<>();

        for (Asset cdf : mission.getCdfList()) {
            snapshots.add(sqNavigator.resolveQuote(cdf));
        }

        for (AssetSnapshot snapshot : snapshots) {
            cdfSeriesList.stream()
                .filter(x -> x.getAsset().equals(snapshot.getAsset())).findFirst()
                .ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
        }

    }

    @Override
    public void stop() {
        sqSession.doLogout();
    }

}

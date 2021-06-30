package com.olexyn.abricore.flow.modes.trade;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
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

    public TradeSqMode(Mission mission) {
        super(mission);
    }

    @Override
    public void start() {
        sqSession = new SqSession();
        sqSession.doLogin();
        sqNavigator = new SqNavigator();
    }

    /**
     * Fetch CDF data from SQ.
     */
    @Override
    public void fetchData() {
        synchronized (Session.class) {
            List<AssetSnapshot> snapshots = new ArrayList<>();
            for (Asset cdf : mission.getCdfList()) {
                snapshots.add(sqNavigator.fetchQuote(cdf));
            }
            SeriesService.putData(snapshots);
        }
    }

    @Override
    public void stop() {
        Session.doLogout();
    }

}

package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.enums.Exchange;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import com.olexyn.abricore.util.enums.Currency;

import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.flow.mission.MissionUtil.isMarketOpen;

public class TradeSqMode extends TradeMode {

    public void start() {

        // get live data

        if (mission.getDerivatives().size() == 0) {
            return;
        }
        SqNavigator sqNavigator = new SqNavigator(null);



        // TODO for now just quote the cdfs, comparison with tw comes later
        Asset cdf = mission.getDerivatives().get(0);
        while (isMarketOpen(sqNavigator.resolveQuote(cdf, null))) {
            AssetSnapshot snapshot = sqNavigator.resolveQuote(cdf, null);
            if (isMarketOpen(snapshot)) {
                getSnapShotSeries(snapshot.getAsset(), snapshot.getInterval())
                    .ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
                // Test conditions.
            }

        }
    }

    @Override
    public void stop() {

    }




    void fetchLiveData() {
        SqNavigator navigator = new SqNavigator(new SqSession().doLogin());
        //navigator.search("XAG");
        navigator.tradeWindow("CH1111950643", Currency.CHF, Exchange.SDOTS);

        List<AssetSnapshot> snapshotList = new ArrayList<>();
        while(true) {
            snapshotList.add(navigator.resolveQuote(null, null));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            navigator.refresh();
        }


    }


    void consultRules() {

    }




}

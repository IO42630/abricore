package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.fingers.sq.SqLogin;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.enums.Exchange;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import com.olexyn.abricore.util.enums.Currency;

import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.flow.mission.MissionUtil.isMarketOpen;

public class TradeMode extends AbstractMode {

    ActionMode actionMode = ActionMode.LIVE;

    @Override
    void start() throws InterruptedException {

        if (mission.getDerivatives().size() == 0) {
            return;
        }


        Asset asset = mission.getUnderlyingAsset();
        Interval interval = mission.getInterval();
        SnapShotSeries snapShotSeries = StoreCsv.read(asset, interval);
        SqNavigator sqNavigator = new SqNavigator(null);

        // TODO
        Double assetPrice = 0d;
        // TODO hotswap between derivatives
        // Asset derivate = mission.getDerivatives().stream().filter( x -> assetPrice - x.getStrike() > 0.7d ).findFirst().orElse(null);


        // TODO for now just quote the cdfs, comparison with tw comes later
        Asset cdf = mission.getDerivatives().get(0);
        while (isMarketOpen(sqNavigator.resolveQuote(cdf, interval))) {
            AssetSnapshot assetSnapshot = sqNavigator.resolveQuote(cdf, interval);
            if (isMarketOpen(assetSnapshot)) {
                snapShotSeries.put(assetSnapshot.getInstant(), assetSnapshot );
                // Test conditions.
            }

        }
    }

    @Override
    void retrieveStoredData() {

    }

    void fetchLiveData() {
        SqNavigator navigator = new SqNavigator(new SqLogin().doLogin());
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

    @Override
    void consultRules() {

    }



}

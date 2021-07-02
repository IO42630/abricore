package com.olexyn.abricore.flow.modes.trade;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import static com.olexyn.abricore.flow.mission.MissionUtil.isMarketOpen;

public class TrainMode extends TradeSqMode {

    public TrainMode(Asset asset, Mission mission) {
        super(mission);
    }

    @Override
    public void fetchData() {

    }

    private Mission mission;

    public void foo() {



        Asset asset = mission.getUnderlyingAsset();
        Interval interval = mission.getInterval();
        Series series = SeriesService.of(asset);
        SqNavigator sqNavigator = new SqNavigator();

        // TODO
        Double assetPrice = 0d;
        // TODO hotswap between derivatives
        // Asset derivate = mission.getDerivatives().stream().filter( x -> assetPrice - x.getStrike() > 0.7d ).findFirst().orElse(null);



        // TODO for now just quote the cdfs, comparison with tw comes later
        Asset cdf = null;
        while (isMarketOpen(sqNavigator.fetchQuote(cdf))) {
            AssetSnapshot assetSnapshot = sqNavigator.fetchQuote(cdf);
            if (isMarketOpen(assetSnapshot)) {
                series.put(assetSnapshot );
                // Test conditions.

            }

        }

        ANum revenue = mission.getRevenue();
        ANum profit = mission.getProfit();
        ANum gain = mission.getGain();
    }


    void retrieveStoredData() {

    }


    void consultRules() {

    }


    void initializeAction() {

    }

    void checkResult() {

    }

    void adjustRules() {

    }

    void storeRules() {

    }


}

package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.Transaction;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.function.Predicate;

import static com.olexyn.abricore.flow.mission.MissionUtil.isMarketOpen;

public class TrainMode extends Mode {

    @Override
    public void init() {

    }

    private Mission mission;

    public void start() {
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

        Long cash = mission.getAllocatedCapital();

        // TODO for now just quote the cdfs, comparison with tw comes later
        Asset cdf = mission.getDerivatives().get(0);
        while (isMarketOpen(sqNavigator.resolveQuote(cdf, interval))) {
            AssetSnapshot assetSnapshot = sqNavigator.resolveQuote(cdf, interval);
            if (isMarketOpen(assetSnapshot)) {
                snapShotSeries.put(assetSnapshot.getInstant(), assetSnapshot );
                // Test conditions.
                for (Predicate<AssetSnapshot> buyCondition : mission.getStrategy().buyConditions) {
                    if (buyCondition.test(assetSnapshot)) {
                        Long size = mission.getStrategy().sizingInCondition.sizeAmount(mission.getAllocatedCapital());
                        Long remainder = cash - size;
                        if (remainder > 0L) {
                            Transaction transaction = new Transaction(mission.getUnderlyingAsset(), assetSnapshot.getInstant(), size, assetSnapshot.getAverage());
                            cash = cash - size;
                            mission.getActiveTransactions().add(transaction);
                        }

                    }
                }
                for (Predicate<AssetSnapshot> sellCondition : mission.getStrategy().sellConditions) {
                    if (sellCondition.test(assetSnapshot)) {
                        for (Transaction transaction : mission.getActiveTransactions()) {
                            transaction.end(assetSnapshot.getInstant(), assetSnapshot.getAverage());
                            cash = cash + transaction.getRevenue();
                            mission.getFinishedTransactions().add(transaction);
                        }
                        mission.getActiveTransactions().removeAll(mission.getFinishedTransactions());
                    }
                }
            }

        }

        Long revenue = mission.getRevenue();
        Long profit = mission.getProfit();
        Long gain = mission.getGain();
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

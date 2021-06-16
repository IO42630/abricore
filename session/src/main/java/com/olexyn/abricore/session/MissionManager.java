package com.olexyn.abricore.session;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.fingers.paper.PaperNavigator;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.function.Predicate;


/**
 * Utility to: <br>
 * - setup Sessions <br>
 * - de-/serialize Sessions <br>
 * - start Sessions <br>
 */
public class MissionManager {

    public static void main(String... args){

        startBacktest(setupSession());

    }

    public static Mission setupSession() {
        // strategy.buyConditions.add(x -> Cross.indicatorACrossesAboveB(
        //     x.getAsset(),
        //     n -> n.getMa().get(R5),
        //     n -> n.getMa().get(R10),
        //     Interval.H_1, x.getInstant()
        // ));
        //
        // strategy.sellConditions.add(x -> Cross.indicatorACrossesAboveB(
        //     x.getAsset(),
        //     n -> n.getMa().get(R10),
        //     n -> n.getMa().get(R5),
        //     Interval.H_1, x.getInstant()
        // ));



        Mission mission = new Mission();
        mission.setAsset(Symbols.getAsset("XAGUSD"));
        mission.setInterval(Interval.H_1);
        mission.setStrategy(StrategyManager.setupStrategy("Test-Strategy"));
        mission.setAllocatedCapital(10000000L);
        return mission;
    }


    public static void startBacktest(Mission mission) {


        SnapShotSeries treeMap = StoreCsv.read(mission.getAsset(), mission.getInterval());

        PaperNavigator paperNavigator = new PaperNavigator();
        paperNavigator.resolveQuote(mission.getAsset(), mission.getInterval());

        Long cash = mission.getAllocatedCapital();

        for (Entry<Instant,AssetSnapshot> entry: treeMap.entrySet()) {
            AssetSnapshot assetSnapshot = entry.getValue();
            for (Predicate<AssetSnapshot> buyCondition : mission.getStrategy().buyConditions) {
                if (buyCondition.test(assetSnapshot)) {
                    Long size = mission.getStrategy().sizingInCondition.sizeAmount(mission.getAllocatedCapital());
                    Long remainder = cash - size;
                    if (remainder > 0L) {
                        Transaction transaction = new Transaction(mission.getAsset(), entry.getKey(), size, assetSnapshot.getAverage());
                        cash = cash - size;
                        mission.getActiveTransactions().add(transaction);
                    }

                }
            }
            for (Predicate<AssetSnapshot> sellCondition : mission.getStrategy().sellConditions) {
                if (sellCondition.test(assetSnapshot)) {
                    for (Transaction transaction : mission.getActiveTransactions()) {
                        transaction.end(entry.getKey(), assetSnapshot.getAverage());
                        cash = cash + transaction.getRevenue();
                        mission.getFinishedTransactions().add(transaction);
                    }
                    mission.getActiveTransactions().removeAll(mission.getFinishedTransactions());
                }
            }
        }
        Long revenue = mission.getRevenue();
        Long profit = mission.getProfit();
        Long gain = mission.getGain();

        int br = 0;
    }


    public static void startLive(Mission mission) {
        // TODO
    }


}

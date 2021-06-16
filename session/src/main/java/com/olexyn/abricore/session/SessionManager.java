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
public class SessionManager {

    public static void main(String... args){

        startBacktest(setupSession());

    }

    public static Session setupSession() {
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



        Session session = new Session();
        session.setAsset(Symbols.getAsset("XAGUSD"));
        session.setInterval(Interval.H_1);
        session.setStrategy(StrategyManager.setupStrategy("Test-Strategy"));
        session.setAllocatedCapital(10000000L);
        return session;
    }


    public static void startBacktest(Session session) {


        SnapShotSeries treeMap = StoreCsv.read(session.getAsset(), session.getInterval());

        PaperNavigator paperNavigator = new PaperNavigator();
        paperNavigator.resolveQuote(session.getAsset(), session.getInterval());

        Long cash = session.getAllocatedCapital();

        for (Entry<Instant,AssetSnapshot> entry: treeMap.entrySet()) {
            AssetSnapshot assetSnapshot = entry.getValue();
            for (Predicate<AssetSnapshot> buyCondition : session.getStrategy().buyConditions) {
                if (buyCondition.test(assetSnapshot)) {
                    Long size = session.getStrategy().sizingInCondition.sizeAmount(session.getAllocatedCapital());
                    Long remainder = cash - size;
                    if (remainder > 0L) {
                        Transaction transaction = new Transaction(session.getAsset(), entry.getKey(), size, assetSnapshot.getAverage());
                        cash = cash - size;
                        session.getActiveTransactions().add(transaction);
                    }

                }
            }
            for (Predicate<AssetSnapshot> sellCondition : session.getStrategy().sellConditions) {
                if (sellCondition.test(assetSnapshot)) {
                    for (Transaction transaction : session.getActiveTransactions()) {
                        transaction.end(entry.getKey(), assetSnapshot.getAverage());
                        cash = cash + transaction.getRevenue();
                        session.getFinishedTransactions().add(transaction);
                    }
                    session.getActiveTransactions().removeAll(session.getFinishedTransactions());
                }
            }
        }
        Long revenue = session.getRevenue();
        Long profit = session.getProfit();
        Long gain = session.getGain();

        int br = 0;
    }


    public static void startLive(Session session) {
        // TODO
    }


}

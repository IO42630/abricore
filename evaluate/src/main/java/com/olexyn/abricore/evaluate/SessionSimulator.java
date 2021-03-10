package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class SessionSimulator {

    public static void main(String... args){
        Asset asset = Symbols.getAsset("XAGUSD");
        Strategy strategy = new Strategy();
        strategy.buyConditions.add(x -> x.getClose() < 20000);
        strategy.sellConditions.add(x -> x.getClose() > 25000);

        simulate(asset, strategy);

    }


    public static void simulate(Asset asset, Strategy strategy) {

        Session session = new Session();

        Interval interval = Interval.H_1;



        SnapShotSeries treeMap = StoreCsv.read(asset, interval);

        for (Entry<Instant,AssetSnapshot> entry: treeMap.entrySet()) {
            AssetSnapshot assetSnapshot = entry.getValue();
            for (Predicate<AssetSnapshot> predicate : strategy.buyConditions) {
                if (predicate.test(assetSnapshot)) {
                    Transaction transaction = new Transaction(asset, entry.getKey(), 10L, assetSnapshot.getAverage());
                    session.getActiveTransactions().add(transaction);
                }
            }
            for (Predicate<AssetSnapshot> predicate : strategy.sellConditions) {
                if (predicate.test(assetSnapshot)) {
                    for (Transaction transaction : session.getActiveTransactions()) {
                        transaction.end(entry.getKey(), assetSnapshot.getAverage());
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
}

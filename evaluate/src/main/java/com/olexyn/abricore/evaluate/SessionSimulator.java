package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Predicate;

public class SessionSimulator {

    void simulate(Asset asset, Strategy strategy) {

        Session session = new Session();

        Interval interval = Interval.H_1;

        TreeMap<Instant, AssetSnapshot> treeMap = asset.getSnapShots(interval);

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
                        session.getActiveTransactions().remove(transaction);
                        session.getFinishedTransactions().add(transaction);
                    }
                }
            }
        }
    }
}

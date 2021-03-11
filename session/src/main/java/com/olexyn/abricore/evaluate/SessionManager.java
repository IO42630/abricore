package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import com.olexyn.abricore.util.Parameters;

import  static com.olexyn.abricore.model.snapshots.RangeEnum.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

        start(setupSession());

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


    public static void start(Session session) {


        SnapShotSeries treeMap = StoreCsv.read(session.getAsset(), session.getInterval());

        for (Entry<Instant,AssetSnapshot> entry: treeMap.entrySet()) {
            AssetSnapshot assetSnapshot = entry.getValue();
            for (Predicate<AssetSnapshot> predicate : session.getStrategy().buyConditions) {
                if (predicate.test(assetSnapshot)) {
                    Transaction transaction = new Transaction(session.getAsset(), entry.getKey(), 10L, assetSnapshot.getAverage());
                    session.getActiveTransactions().add(transaction);
                }
            }
            for (Predicate<AssetSnapshot> predicate : session.getStrategy().sellConditions) {
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

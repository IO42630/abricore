package com.olexyn.abricore.session.calc;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Levels {

    public static void main(String... args) {
        Asset asset = Symbols.getAsset("XAGUSD");
        SnapShotSeries snapShotSeries = StoreCsv.getInstance().read(asset, Interval.H_1);

        Long max = 0L;


        for (Entry<Instant, AssetSnapshot> entry : snapShotSeries.entrySet()) {
            if (entry.getValue().getHigh() > max) {
                max = entry.getValue().getHigh();
            }
        }

        int br = 0;

        TreeMap<Integer, Integer> values = new TreeMap<>();
        for (Entry<Instant, AssetSnapshot> entry : snapShotSeries.entrySet()) {

            AssetSnapshot snapshot = entry.getValue();
            long level = (snapshot.getOpen() + snapshot.getClose() ) / 2;
            int lvl = (int) level / 1000;

            if ( values.containsKey(lvl)) {
                values.put(lvl, values.get(lvl)+1);
            } else {
                values.put(lvl, 1);
            }
            if (lvl > 50) {
                int dbr = 2;
            }

            br = 0;
        }
        br = 0;


        int sideload = 0;
        for (Entry<Integer, Integer> entry :values.descendingMap().entrySet()) {
            sideload = entry.getValue() + sideload;
            entry.setValue(sideload);
        }

        br= 0;

    }
}

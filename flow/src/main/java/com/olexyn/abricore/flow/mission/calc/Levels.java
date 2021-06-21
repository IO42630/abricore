package com.olexyn.abricore.flow.mission.calc;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Levels {

    public static void main(String... args) {
        Asset asset = AssetService.ofName("XAGUSD");
        Series series = SeriesService.of(asset);

        ANum max = new ANum(0,0);


        for (Entry<Instant, AssetSnapshot> entry : series.entrySet()) {
            // if (entry.getValue().getHigh() > max) {
            //     max = entry.getValue().getHigh();
            // }
        }

        int br = 0;

        TreeMap<Integer, Integer> values = new TreeMap<>();
        for (Entry<Instant, AssetSnapshot> entry : series.entrySet()) {

            AssetSnapshot snapshot = entry.getValue();
            // long level = (snapshot.getOpen() + snapshot.getClose() ) / 2;
            long level = 0;
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

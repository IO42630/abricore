package com.olexyn.abricore.flow.mission.calc;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.GetFromSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import  static com.olexyn.abricore.model.snapshots.RangeEnum.*;



import java.time.Instant;

public class Cross {

    public static void main(String... args) {
        Asset asset = Symbols.getAsset("XAGUSD");
        indicatorACrossesAboveB(
            asset,
            x -> x.getMa().get(R5),
            x -> x.getHighBol().get(R20),
            Interval.H_1,
            Instant.now()
        );

    }

    /**
     * Represents two asset quotes crossing on the chart.
     */
   static boolean indicatorACrossesAboveB(
       Asset asset1,
       GetFromSnapshot indicatorA,
       GetFromSnapshot indicatorB,
       Interval interval,
       Instant instant) {

       SnapShotSeries series = StoreCsv.read(asset1, interval);

       if (series.approximateKey(instant) == null) {
           return false;
       }
       instant = series.approximateKey(instant);

       if (series.higherEntry(instant) == null || series.lowerEntry(instant) == null) {
           return false;
       }

       AssetSnapshot next = series.higherEntry(instant).getValue();
       AssetSnapshot prev = series.lowerEntry(instant).getValue();

       if (next == null
           || prev == null
           || indicatorA.get(next) == null
           || indicatorB.get(next) == null
           || indicatorA.get(prev) == null
           || indicatorB.get(prev) == null) {
           return false;
       }

       return indicatorA.get(next) > indicatorB.get(next)
           && indicatorA.get(prev) < indicatorB.get(prev);
   }
}

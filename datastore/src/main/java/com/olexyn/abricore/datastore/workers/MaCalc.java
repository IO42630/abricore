package com.olexyn.abricore.datastore.workers;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.IndicatorRange;

import java.time.Instant;
import java.util.TreeMap;

public class MaCalc {

    public void calcAllMa(Asset asset, Interval interval) {
        for (IndicatorRange indicatorRange : IndicatorRange.values()) {
            calculateMa(asset, interval, indicatorRange);
        }
    }

    public void calculateMa(Asset asset, Interval interval, IndicatorRange range) {
        TreeMap<Instant, AssetSnapshot> treeMap = StoreCsv.getInstance().read(asset, interval);
        int rangeValue = range.getValue();

        //  move the frameEnd to its starting position
        Instant frameEndKey = treeMap.firstKey();
        long sum = 0L;
        for (int i = 0; i < rangeValue; i++) {
            Long frameEndAvg = treeMap.get(frameEndKey).getAverage();
            if (frameEndAvg == null) {
                throw new CalcException();
            }
            sum += frameEndAvg;
            incrementKey(treeMap, frameEndKey);
        }

        // move the frame and set the MAs
        Instant frameStartKey = treeMap.firstKey();
        while (!frameEndKey.equals(treeMap.lastKey())) {
            Long frameStartAverage = treeMap.get(frameStartKey).getAverage();
            Long frameEndAverage = treeMap.get(frameEndKey).getAverage();

            sum = sum - frameStartAverage + frameEndAverage;
            treeMap.get(frameEndKey).getMa().set(range, sum / rangeValue);

            incrementKey(treeMap, frameStartKey);
            incrementKey(treeMap, frameEndKey);
        }
    }

    private void incrementKey(TreeMap<Instant,AssetSnapshot> treeMap, Instant key) {
        key = treeMap.ceilingKey(key);
    }


    public void calcBol(Asset asset, Interval interval, IndicatorRange indicatorRange) {


        // we already have the ma
        // for first range, get all
        // high - avg = diff
        // sumX = sum ( diff^2 )
        // sqrt ( sumX /range )

        // as it is very similar to ma, clean up calcMa first.

        // for lowBol same but with avg - low;



        TreeMap<Instant, AssetSnapshot> treeMap = StoreCsv.getInstance().read(asset, interval);
        int range = indicatorRange.getValue();

        Instant firstKey = treeMap.firstKey();
        Instant previousKey = treeMap.firstKey();
        Instant nextKey = null;

        long sum = 0L;
        for (int i = 0; i < range; i++) {
            Long previousAverage = treeMap.get(previousKey).getAverage();
            if (previousAverage == null ) {
                // null
            }
            sum = sum + previousAverage;
            nextKey = treeMap.ceilingKey(previousKey);

        }

        long ma = sum / range;
        treeMap.get(nextKey).getMa().set(indicatorRange, ma);


        Instant frameStartKey = firstKey;
        Instant frameEndKey = nextKey;
        while (!frameEndKey.equals(treeMap.lastKey())) {

            Long frameStartAverage = treeMap.get(frameStartKey).getAverage();
            Long frameEndAverage = treeMap.get(frameEndKey).getAverage();

            sum = sum -frameStartAverage + frameEndAverage;
            ma = sum / range;

            frameStartKey = treeMap.ceilingKey(frameStartKey);
            frameEndKey = treeMap.ceilingKey(frameEndKey);

            treeMap.get(frameStartKey).getMa().set(indicatorRange, ma);

        }

    }
}

package com.olexyn.abricore.datastore.batch;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.IndicatorRange;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;

public class MaCalcBatch {

    public void calcAllMa(Asset asset, Interval interval) {
        for (IndicatorRange indicatorRange : IndicatorRange.values()) {
            calculateMa(asset, interval, indicatorRange);
        }
    }

    public void calculateMa(Asset asset, Interval interval, IndicatorRange range) {
        SnapShotSeries treeMap = StoreCsv.read(asset, interval);
        int rangeValue = range.getNum();

        //  move the frameEnd to its starting position
        Instant frameEndKey = treeMap.firstKey();
        long sum = 0L;
        for (int i = 0; i < rangeValue; i++) {
            Long frameEndAvg = treeMap.get(frameEndKey).getAverage();
            if (frameEndAvg == null) {
                throw new CalcException();
            }
            sum += frameEndAvg;
            frameEndKey = incrementKey(treeMap, frameEndKey);
        }

        // move the frame and set the MAs
        Instant frameStartKey = treeMap.firstKey();
        while (!frameEndKey.equals(treeMap.lastKey())) {
            Long frameStartAverage = treeMap.get(frameStartKey).getAverage();
            Long frameEndAverage = treeMap.get(frameEndKey).getAverage();

            sum = sum - frameStartAverage + frameEndAverage;
            treeMap.get(frameEndKey).getMa().set(range, sum / rangeValue);


            frameStartKey = incrementKey(treeMap, frameStartKey);
            frameEndKey = incrementKey(treeMap, frameEndKey);
        }
        StoreCsv.update(treeMap);
    }

    private Instant incrementKey(SnapShotSeries treeMap, Instant key) {
        return treeMap.higherKey(key);
    }
}

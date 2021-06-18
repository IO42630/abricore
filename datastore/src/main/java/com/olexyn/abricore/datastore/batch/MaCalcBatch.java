package com.olexyn.abricore.datastore.batch;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.datastore.StoreCsvService;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.RangeEnum;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;

public class MaCalcBatch {

    public void calcAllMa(Asset asset, Interval interval) {
        for (RangeEnum rangeEnum : RangeEnum.values()) {
            calculateMa(asset, interval, rangeEnum);
        }
    }

    public void calculateMa(Asset asset, Interval interval, RangeEnum range) {
        SnapShotSeries treeMap = SnapSeriesService.of(asset, interval);
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
        StoreCsvService.update(treeMap);
    }

    private Instant incrementKey(SnapShotSeries treeMap, Instant key) {
        return treeMap.higherKey(key);
    }
}

package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.HashSet;
import java.util.Set;

public class StoreCache {

    private final static Set<SnapShotSeries> CACHED_SNAPSHOT_SERIES_COLLECTION = new HashSet<>();

    public static SnapShotSeries getSnapShotSeries(Asset asset, Interval interval) {
        for (SnapShotSeries snapShotSeries   : CACHED_SNAPSHOT_SERIES_COLLECTION) {
            if (snapShotSeries.getAsset().equals(asset)
            &&snapShotSeries.getInterval().equals(interval)) {
                return snapShotSeries;
            }
        }
        return  null;
    }

    public static Set<SnapShotSeries> getCachedSnapshotSeriesCollection() {
        return CACHED_SNAPSHOT_SERIES_COLLECTION;
    }
}

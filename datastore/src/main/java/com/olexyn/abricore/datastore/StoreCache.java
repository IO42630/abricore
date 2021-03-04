package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class StoreCache {

    public static Set<SnapShotSeries> cachedSnapShotSeries = new HashSet<>();

    public static SnapShotSeries getSnapShotSeries(Asset asset, Interval interval) {
        for (SnapShotSeries snapShotSeries   : cachedSnapShotSeries) {
            if (snapShotSeries.getAsset().equals(asset)
            &&snapShotSeries.getInterval().equals(interval)) {
                return snapShotSeries;
            }
        }
        return  null;
    }
}

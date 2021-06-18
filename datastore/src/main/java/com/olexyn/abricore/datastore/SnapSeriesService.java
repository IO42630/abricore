package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SnapSeriesService {

    private SnapSeriesService() {}

    private final static Set<SnapShotSeries> SERIES_COLLECTION = new HashSet<>();

    /**
     * Try to read from Cache. Otherwise read from Disk.
     */
    public static SnapShotSeries of(Asset asset, Interval interval) {
        if (getSeries(asset, interval).isEmpty()) {
            SERIES_COLLECTION.add(StoreCsvService.readFromDisk(asset, interval));
        }
        return getSeries(asset, interval).get();
    }

    private static Optional<SnapShotSeries> getSeries(Asset asset, Interval interval) {
        return SERIES_COLLECTION.stream()
            .filter(
                x -> x.getAsset().equals(asset)
                && x.getInterval().equals(interval)
            )
            .findFirst();
    }

    public static void save(SnapShotSeries series) {
        StoreCsvService.update(series);
    }

}

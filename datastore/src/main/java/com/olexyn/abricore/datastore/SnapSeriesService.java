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
    public static SnapShotSeries of(Asset asset) {
        if (getSeries(asset).isEmpty()) {
            SERIES_COLLECTION.add(StoreCsvService.readFromDisk(asset));
        }
        return getSeries(asset).get();
    }

    private static Optional<SnapShotSeries> getSeries(Asset asset) {
        return SERIES_COLLECTION.stream()
            .filter(x -> x.getAsset().equals(asset))
            .findFirst();
    }

    public static void save(SnapShotSeries series) {
        StoreCsvService.update(series);
    }

}

package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SeriesService {

    private SeriesService() {}

    private final static Set<Series> SERIES_COLLECTION = new HashSet<>();

    /**
     * Try to read from Cache. Otherwise read from Disk.
     */
    public static Series of(Asset asset) {
        if (getSeries(asset).isEmpty()) {
            SERIES_COLLECTION.add(StoreCsvService.readFromStoreCsv(asset));
        }
        return getSeries(asset).get();
    }

    public static void add(Asset asset) {
        if (getSeries(asset).isEmpty()) {
            SERIES_COLLECTION.add(new Series(asset));
        }
    }

    private static Optional<Series> getSeries(Asset asset) {
        return SERIES_COLLECTION.stream()
            .filter(x -> x.getAsset().equals(asset))
            .findFirst();
    }

    public static void putData(List<AssetSnapshot> snapshots) {
        for (AssetSnapshot snapshot : snapshots) {
            SERIES_COLLECTION.stream()
                .filter(x -> x.getAsset().equals(snapshot.getAsset())).findFirst()
                .ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
        }
    }

    public static void save(Series series) {
        StoreCsvService.update(series);
    }

}

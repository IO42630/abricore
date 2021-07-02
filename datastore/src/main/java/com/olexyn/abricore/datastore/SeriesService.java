package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.LogUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class SeriesService {

    private static final Logger LOGGER = LogUtil.get(SeriesService.class);

    public static void loadSeries(Set<Asset> assets) {
        LOGGER.info("START populating SERIES from Store.");
        for (Asset asset : assets) {
            of(asset);
        }
        LOGGER.info("END populating SERIES from Store.");
    }

    private final static Set<Series> SERIES = new HashSet<>();

    /**
     * Try to read from Cache. Otherwise read from Disk.
     * If no CSV is found add new Series.
     */
    public synchronized static Series of(Asset asset) {
        if (getSeries(asset).isEmpty()) {
            SERIES.add(StoreCsvService.readFromStoreCsv(asset));
        }
        return getSeries(asset).get();
    }

    private static Optional<Series> getSeries(Asset asset) {
        return SERIES.stream()
            .filter(x -> x.getAsset().equals(asset))
            .findFirst();
    }

    public static void putData(List<AssetSnapshot> snapshots) {
        for (AssetSnapshot snapshot : snapshots) {
            of(snapshot.getAsset()).put(snapshot.getInstant(), snapshot);
        }
    }

    public static void save(Series series) {
        StoreCsvService.update(series);
    }

    public static ANum getLastTraded(Asset asset) {
        ANum lastTraded;
        synchronized (SeriesService.class) {
            lastTraded = SeriesService.of(asset).getLast().getPrice().getTraded();
        }
        return lastTraded;
    }

}

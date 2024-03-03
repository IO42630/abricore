package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.store.dao.SnapshotDao;
import com.olexyn.abricore.util.exception.MissingException;
import com.olexyn.abricore.util.log.LogU;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * ProtoSeriesService is a Service that manages a Set of Series.
 */
@Service
public abstract class ProtoSeriesService implements ISeriesService {

    private final Map<AssetDto, Series> seriesMap = new HashMap<>();
    private final SnapshotDao snapshotDao;
    private final AssetService assetService;

    @Autowired
    protected ProtoSeriesService(
        SnapshotDao snapshotDao,
        AssetService assetService
    ) {
        this.snapshotDao = snapshotDao;
        this.assetService = assetService;
    }

    /**
     * We use this Override to make Methods from SeriesService apply to PaperSeriesService.SERIES_SET, <br>
     * if called from an instance of PaperSeriesService.
     */
    protected Map<AssetDto, Series> getSeriesMap() {
        return seriesMap;
    }

    /**
     * This method is called when a new Series is needed.
     */
    @Override
    public synchronized Series of(@Nullable AssetDto asset) {
        if (asset == null) {
            LogU.warnPlain("asset is null");
            throw new MissingException("asset is null");
        }
        if (getSeries(asset) == null) {
            addNewSeries(asset);
        }
        return getSeries(asset);
    }

    protected void addNewSeries(AssetDto asset) {
        getSeriesMap().put(asset, new Series(asset));
    }

    @Override
    public synchronized Series ofFull(AssetDto asset) {
        var series = of(asset);
        LogU.infoStart("loading data for %s from DB", asset);
        snapshotDao.findAllByAsset(asset)
            .peek(snap -> snap.setSeries(series))
            .forEach(series::putNoWait);
        LogU.infoEnd("%s records found for %s", series.size(), asset);
        return series;
    }

    /**
     * Try to read from Cache. Otherwise, read from Disk.
     * If no CSV is found add new Asset.
     */
    @Override
    public synchronized @Nullable Series of(AssetDto asset, Instant from, Instant to) {
        var series = of(asset);
        if (series == null) { return null; }
        if (!series.isEmpty()
            && series.getFirstKey() != null
            && series.getLastKey() != null
            && (series.getFirstKey().equals(from) || series.getFirstKey().isBefore(from))
            && (series.getLastKey().equals(to) || series.getLastKey().isAfter(to))
        ) {
            return series;
        }
        return mergeFromDb(series, from, to);
    }

    @Override
    public synchronized @Nullable Series of(AssetDto asset, Instant to, Duration duration) {
        return of(asset, to.minus(duration), to);
    }

    private synchronized Series mergeFromDb(Series series, Instant from, Instant to) {
        var asset = series.getAsset();
        LogU.infoStart("loading data for %s from DB", asset);
        snapshotDao.getSegment(asset, from, to)
            .peek(snap -> snap.setSeries(series))
            .forEach(series::putNoWait);
        LogU.infoEnd("%s records found for %s", series.size(), asset);
        return series;
    }

    protected Series getSeries(AssetDto asset) {
        return getSeriesMap().get(asset);
    }

    /**
     * Not synchronized, since the individual PUT is synchronized.
     */
    @Override
    public void putData(Set<SnapshotDto> snapshots) {
        snapshots.stream()
            .map(snap -> new SimpleEntry<>(snap, of(snap.getAsset())))
            .filter(entry -> entry.getValue() != null)
            .forEach(entry -> entry.getValue().put(entry.getKey()));
    }

    @Override
    public synchronized void save() {
        for (var series : getSeriesMap().values()) {
            snapshotDao.save(series);
        }
    }

    public synchronized void initCache() {
        assetService.getAssets().forEach(
            assetDto -> of(assetDto, Instant.now().minus(Duration.ofDays(2000)), Instant.now())
        );
    }

    public synchronized void patch() {
        assetService.getAssets().stream()
            .map(assetDto -> of(assetDto, Instant.now().minus(Duration.ofDays(2000)), Instant.now()))
            .filter(Objects::nonNull)
            .forEach(Series::patch);
    }

    public synchronized long getLastTraded(AssetDto asset) {
        var snap = getLast(asset);
        if (snap == null) { return 0; }
        return snap.getTradePrice();
    }

    @Override
    public synchronized @Nullable SnapshotDto getLast(AssetDto asset) {
        var series = of(asset);
        if (series == null) { return null; }
        var snap = series.getLast();
        if (snap != null) { return snap; }
        return findLast(asset, Instant.now());
    }

    private @Nullable SnapshotDto findLast(AssetDto asset, Instant anchor) {
        var days = 2;
        var series = of(asset, anchor.minus(Duration.ofDays(days)), anchor);
        if (series == null) { return null; }
        while (series.isEmpty() && days < 365) {
            days *= 2;
            series = of(asset, anchor.minus(Duration.ofDays(days)), anchor);
            if (series == null) { return null; }
        }
        return series.isEmpty() ? null : series.getLast();
    }

    public synchronized long getLastBid(AssetDto asset) {
        var snap = getLast(asset);
        if (snap == null) { return 0; }
        return snap.getBidPrice();
    }

    public synchronized long getLastAsk(AssetDto asset) {
        var snap = getLast(asset);
        if (snap == null) { return 0; }
        return snap.getAskPrice();
    }

    public synchronized long getLastSpread(AssetDto asset) {
        var lastAsk = getLastAsk(asset);
        var lastBid = getLastBid(asset);
        if (lastAsk != 0 && lastBid != 0) {
            return lastAsk - lastBid;
        }
        return 0;
    }

}

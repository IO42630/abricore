package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.store.dao.SnapshotDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * PaperSeriesService wraps a SeriesService and adds a PaperSeries for each Series in the SeriesService.
 * This way each PaperSeries is backed by a Series.
 */
@Scope("prototype")
@Service
public class PaperSeriesService extends ProtoSeriesService {

    private final SeriesService baseSeriesService;
    private final Map<AssetDto, Series> paperSeriesSet = new HashMap<>();
    private final int sampleSize;

    @Autowired
    public PaperSeriesService(
        SeriesService baseSeriesService,
        SnapshotDao snapshotDao,
        AssetService assetService,
        EventService eventService
    ) {
        super(snapshotDao, assetService, eventService);
        this.baseSeriesService = baseSeriesService;
        this.sampleSize = eventService.describeInt("series.calc.sample.size.paper");
    }

    @Override
    protected Map<AssetDto, Series> getSeriesMap() {
        return paperSeriesSet;
    }

    @Override
    protected void addNewSeries(AssetDto asset) {
        var baseSeries = baseSeriesService.of(asset);
        if (baseSeries == null) { return; }
        getSeriesMap().put(asset, new Series(asset, sampleSize));
    }

    @Override
    public synchronized void save() { /* NOP */ }

}

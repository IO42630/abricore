package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Duration;

public abstract class ObserveMode extends Mode {

    public ObserveMode(Asset asset) {
        underlyingSeries = SnapSeriesService.of(asset);
    }

    public void addAsset(Asset asset) {
        super.addAsset(asset);
    }



}

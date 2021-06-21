package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Duration;

public abstract class ObserveMode extends Mode {

    public void addAsset(Asset asset) {
        super.addAsset(asset);
    }


}

package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

public class StockSnapshot extends AssetSnapshot {
    public StockSnapshot(Asset asset, Interval interval) {
        super(asset, interval);
    }
}

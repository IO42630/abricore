package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

public class EtfSnapshot extends AssetSnapshot {
    public EtfSnapshot(Asset asset, Interval interval) {
        super(asset, interval);
    }
}

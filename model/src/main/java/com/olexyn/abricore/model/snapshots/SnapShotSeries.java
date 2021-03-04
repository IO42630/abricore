package com.olexyn.abricore.model.snapshots;

import java.time.Instant;
import java.util.TreeMap;

public class SnapShotSeries {

    private TreeMap<Instant, AssetSnapshot> treeMap;


    AssetSnapshot get(Instant instant) {
        return treeMap.get(instant);
    }

    AssetSnapshot put(Instant instant, AssetSnapshot snapshot) {
        snapshot.setSeries(this);
        return treeMap.put(instant, snapshot);
    }
}

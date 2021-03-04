package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class SnapShotSeries {

    private TreeMap<Instant, AssetSnapshot> treeMap;

    private Asset asset;
    private Interval interval;

    public SnapShotSeries(Asset asset, Interval interval) {
        this.asset = asset;
        this.interval = interval;
    }

    public Asset getAsset() {
        return asset;
    }

    public Interval getInterval() {
        return interval;
    }

    public AssetSnapshot get(Instant instant) {
        return treeMap.get(instant);
    }

    public AssetSnapshot put(Instant instant, AssetSnapshot snapshot) {
        snapshot.setSeries(this);
        return treeMap.put(instant, snapshot);
    }

    public Set<Entry<Instant, AssetSnapshot>> entrySet() {
        return treeMap.entrySet();
    }

    public Entry<Instant, AssetSnapshot> higherEntry(Instant key) {
        return treeMap.higherEntry(key);
    }

    public Entry<Instant, AssetSnapshot> lowerEntry(Instant key) {
        return treeMap.lowerEntry(key);
    }

    public void clear() {
        treeMap.clear();
    }

    public Entry<Instant, AssetSnapshot> firstEntry() {
        return treeMap.firstEntry();
    }

    public boolean containsKey(Instant key) {
        return treeMap.containsKey(key);
    }

    public Instant firstKey() {
        return treeMap.firstKey();
    }

    public Instant lastKey() {
        return treeMap.lastKey();
    }

    public Instant ceilingKey(Instant key) {
        return treeMap.ceilingKey(key);
    }

    public Instant higherKey(Instant key) {
        return treeMap.higherKey(key);
    }
}

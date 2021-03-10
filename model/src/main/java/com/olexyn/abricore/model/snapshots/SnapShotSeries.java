package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class SnapShotSeries {

    private final TreeMap<Instant, AssetSnapshot> treeMap = new TreeMap<>();

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

    public Instant getFirstAfter(Instant instant) {
        // "lowerKey" "instant" "i"
        for (Instant i : treeMap.keySet()) {
            if (i.isAfter(instant) && treeMap.lowerKey(i).isBefore(instant)) {
                return i;
            }
        }
        return null;
    }

    public Instant getFirstBefore(Instant instant) {
        // "i" "instant" "higherKey"
        for (Instant i : treeMap.keySet()) {
            if (i.isBefore(instant) && treeMap.higherKey(i).isBefore(instant)) {
                return i;
            }
        }
        return null;
    }

    /**
     * @return returns a sequence of the Series between "from" and "to"
     */
    private SnapShotSeries limitSeries(Instant from, Instant to) {
        SnapShotSeries limitedSeries = new SnapShotSeries(getAsset(), getInterval());
        Instant first = getFirstAfter(from);
        Instant last = getFirstBefore(to);
        limitedSeries.put(first, limitedSeries.get(first));
        while(higherKey(first) != last) {
            first = higherKey(first);
            limitedSeries.put(first, limitedSeries.get(first));
        }
        limitedSeries.put(last, limitedSeries.get(last));
        return limitedSeries;
    }

    public AssetSnapshot put(Instant instant, AssetSnapshot snapshot) {
        snapshot.setSeries(this);
        return treeMap.put(instant, snapshot);
    }

    public Set<Entry<Instant, AssetSnapshot>> entrySet() {
        return treeMap.entrySet();
    }

    public int size() {
        return treeMap.size();
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

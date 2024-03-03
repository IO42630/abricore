package com.olexyn.abricore.model.runtime.snapshots;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.util.log.LogU;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * Extract the basic wrapper for the TreeMap and HashMap here.
 */
public abstract class ProtoSeries {

    private static final String IS_EMPTY = " is empty.";


    private final HashMap<Instant, SnapshotDto> hashMap = new LinkedHashMap<>();

    private final TreeMap<Instant, SnapshotDto> treeMap = new TreeMap<>();

    public abstract AssetDto getAsset();

    protected HashMap<Instant, SnapshotDto> hashMap() {
        return hashMap;
    }

    protected TreeMap<Instant, SnapshotDto> tree() {
        return treeMap;
    }


    public @Nullable Instant getFirstKey() {
        if (hashMap.isEmpty()) {
            LogU.warnPlain("Series for " + getAsset() + IS_EMPTY);
            return null;
        }
        Instant firstKey = treeMap.firstKey();
        if (firstKey == null) {
            LogU.warnPlain("First KEY of Series for " + getAsset() + IS_EMPTY);
            return null;
        }
        return firstKey;
    }

    public @Nullable Instant getLastKey() {
        if (hashMap.isEmpty()) {
            LogU.warnPlain("Series for " + getAsset() + IS_EMPTY);
            return null;
        }
        Instant lastKey = treeMap.lastKey();
        if (lastKey == null) {
            LogU.warnPlain("Last KEY of Series for " + getAsset() + IS_EMPTY);
            return null;
        }
        return lastKey;
    }


    public @Nullable SnapshotDto getLast() {
        Instant lastKey = getLastKey();
        if (lastKey == null) { return null; }
        return hashMap.get(getLastKey());
    }



    public Instant getSameOrFirstBefore(Instant instant) {
        if (hashMap.containsKey(instant)) { return instant; }
        return treeMap.lowerKey(instant);
    }

    public Instant getSameOrFirstAfter(Instant instant) {
        if (hashMap.containsKey(instant)) { return instant; }
        return higherKey(instant);
    }


    public Instant higherKey(Instant key) {
        return treeMap.higherKey(key);
    }

    public Instant lowerKey(Instant key) {
        return treeMap.higherKey(key);
    }





    public NavigableSet<Instant> getNavSet() {
        return treeMap.navigableKeySet();
    }


    public int size() { return treeMap.size(); }

    public boolean isEmpty() { return size() == 0; }

    public void clear() {
        hashMap.clear();
        treeMap.clear();
    }

}

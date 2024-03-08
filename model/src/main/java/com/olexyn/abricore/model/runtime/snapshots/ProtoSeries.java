package com.olexyn.abricore.model.runtime.snapshots;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.util.log.LogU;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * Extract the basic wrapper for the TreeMap and HashMap here.
 */
public abstract class ProtoSeries implements ISeries {

    private static final String IS_EMPTY = " is empty.";


    private final HashMap<Instant, SnapshotDto> hashMap = new LinkedHashMap<>();

    private final TreeMap<Instant, SnapshotDto> treeMap = new TreeMap<>();

    @Getter
    @Setter
    @Nullable
    private Instant lastPutKey = null;



    protected HashMap<Instant, SnapshotDto> hashMap() {
        return hashMap;
    }

    protected TreeMap<Instant, SnapshotDto> tree() {
        return treeMap;
    }


    @Override
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

    @Override
    public @Nullable Instant getLastKey() {
        return getLastPutKey();
    }


    @Override
    public @Nullable SnapshotDto getLast() {
        var lastKey = getLastKey();
        if (lastKey == null) { return null; }
        return hashMap.get(lastKey);
    }



    @Override
    public Instant getSameOrFirstBefore(Instant instant) {
        if (hashMap.containsKey(instant)) { return instant; }
        return treeMap.lowerKey(instant);
    }

    @Override
    public Instant getSameOrFirstAfter(Instant instant) {
        if (hashMap.containsKey(instant)) { return instant; }
        return higherKey(instant);
    }


    @Override
    public Instant higherKey(Instant key) {
        return treeMap.higherKey(key);
    }

    @Override
    public Instant lowerKey(Instant key) {
        return treeMap.higherKey(key);
    }

    @Override
    public NavigableSet<Instant> getNavSet() {
        return treeMap.navigableKeySet();
    }


    @Override
    public int size() { return treeMap.size(); }

    public boolean isEmpty() { return size() == 0; }

    @Override
    public void clear() {
        hashMap.clear();
        treeMap.clear();
    }

}

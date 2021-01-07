package com.olexyn.abricore.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class Asset {

    private final Map<Interval,TreeMap<Instant, AssetSnapshot>> snapshots = new HashMap<>();

    public Asset() {
        for (Interval interval : Interval.values()) {
            snapshots.put(interval, new TreeMap<>());
        }
    }

    public TreeMap<Instant, AssetSnapshot> getSnapShots(Interval interval) {
        return snapshots.get(interval);
    }





    public TreeMap<Instant, Double> calculateMASeries(
        Interval interval,
        int amount,
        Instant seriesStart,
        Instant seriesEnd) throws Exception {

        TreeMap<Instant, Double> series = new TreeMap<>();

        for (Instant i = seriesStart; i.isBefore(seriesEnd); i= i.plus(interval.size)) {
            series.put(i, calculateMA(interval, amount, i));
        }
        return series;
    }



    double calculateMA(Interval interval, int amount, Instant lastInstant) throws Exception {

        TreeMap<Instant, AssetSnapshot> snapshots = getSnapShots(interval);
        Entry<Instant, AssetSnapshot> currentSnapshotEntry = null;

        for (Entry<Instant, AssetSnapshot> entry : snapshots.entrySet()) {
            if (entry.getKey().equals(lastInstant)) {
                currentSnapshotEntry = entry;
                break;
            }
        }
        if (currentSnapshotEntry == null) throw new Exception("Instant of desired MA not available in snapshots.");

        BigDecimal sum = new BigDecimal(0);

        for (int i=0; i < amount; i++) {
            if (currentSnapshotEntry == null) throw new Exception("Not enough values to calculate MA.");
            sum = sum.add(BigDecimal.valueOf(currentSnapshotEntry.getValue().getPrice()));
            currentSnapshotEntry = snapshots.lowerEntry(currentSnapshotEntry.getKey());
        }
        return sum.divide(BigDecimal.valueOf(amount), RoundingMode.HALF_EVEN).doubleValue();
    }
}

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
        Entry<Instant, AssetSnapshot> currentSnapshotEntry = getEntryFromInstant(snapshots, lastInstant);


        if (currentSnapshotEntry == null) throw new Exception("Instant of desired MA not available in snapshots.");

        BigDecimal sum = new BigDecimal(0);

        for (int i=0; i < amount; i++) {
            if (currentSnapshotEntry == null) throw new Exception("Not enough values to calculate MA.");
            sum = sum.add(BigDecimal.valueOf(currentSnapshotEntry.getValue().getPrice()));
            currentSnapshotEntry = snapshots.lowerEntry(currentSnapshotEntry.getKey());
        }
        return sum.divide(BigDecimal.valueOf(amount), RoundingMode.HALF_EVEN).doubleValue();
    }

    enum Extreme {
        MIN,
        MAX
    }

    Entry<Instant, AssetSnapshot> getEntryFromInstant(TreeMap<Instant, AssetSnapshot> snapshots, Instant instant) {
        for (Entry<Instant, AssetSnapshot> entry : snapshots.entrySet()) {
            if (entry.getKey().equals(instant)) {
                return entry;
            }
        }
        return null;
    }

    TreeMap<Instant, AssetSnapshot> calculateExtremes(Interval interval, int radius, Instant seriesStart, Instant seriesEnd, Extreme extremeType) {

        TreeMap<Instant, AssetSnapshot> snapshots = getSnapShots(interval);
        TreeMap<Instant, AssetSnapshot> extremes = new TreeMap<>();

        double flip;
        if (extremeType == Extreme.MAX) { flip = 1; } else { flip = -1; }

        Entry<Instant, AssetSnapshot> candidateSnapshotEntry = getEntryFromInstant(snapshots, seriesStart);
        boolean isExtreme = true;

        while (candidateSnapshotEntry.getKey().isBefore(seriesEnd)) {
            Entry<Instant, AssetSnapshot> ceilingRadiusSnapshotEntry = candidateSnapshotEntry;
            Entry<Instant, AssetSnapshot> floorRadiusSnapshotEntry = candidateSnapshotEntry;

            for (int i = 0; i < radius; i++) {
                try {
                    if (candidateSnapshotEntry.getValue().getPrice() * flip >= flip * ceilingRadiusSnapshotEntry.getValue().getPrice()
                        && candidateSnapshotEntry.getValue().getPrice() * flip >= flip * floorRadiusSnapshotEntry.getValue().getPrice()) {
                        ceilingRadiusSnapshotEntry = snapshots.higherEntry(ceilingRadiusSnapshotEntry.getKey());
                        floorRadiusSnapshotEntry = snapshots.lowerEntry(floorRadiusSnapshotEntry.getKey());
                    } else {
                        isExtreme = false;
                    }
                } catch (NullPointerException ignored) {}
            }

            if (isExtreme) {
                extremes.put(candidateSnapshotEntry.getKey(), candidateSnapshotEntry.getValue());
            }

            try {
                candidateSnapshotEntry = snapshots.higherEntry(ceilingRadiusSnapshotEntry.getKey());
            } catch (NullPointerException e) {
                break;
            }
        }
        return extremes;
    }



}

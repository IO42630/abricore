package com.olexyn.abricore.evaluate.calc;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;
import java.util.Map.Entry;

public class Max {

    public static void calcGlobalMax(String... args) {
        Asset asset = Symbols.getAsset("XAGUSD");
        SnapShotSeries treeMap = StoreCsv.getInstance().readFromCache(asset, Interval.H_1);

        Long max = 0L;


        for (Entry<Instant, AssetSnapshot> entry : treeMap.entrySet()) {
            if (entry.getValue().getHigh() > max) {
                max = entry.getValue().getHigh();
            }
        }


    }

    public SnapShotSeries calcMaximas(SnapShotSeries baseSeries, int radius, Instant seriesStart, Instant seriesEnd) {
        return calc( baseSeries,  radius,  seriesStart,  seriesEnd,  Extreme.MAX);
    }

    public SnapShotSeries calcMinimas(SnapShotSeries baseSeries, int radius, Instant seriesStart, Instant seriesEnd) {
        return calc( baseSeries,  radius,  seriesStart,  seriesEnd,  Extreme.MIN);
    }


    private SnapShotSeries calc(SnapShotSeries baseSeries, int radius, Instant seriesStart, Instant seriesEnd, Extreme extremeType) {

        SnapShotSeries extremes = new SnapShotSeries(baseSeries.getAsset(), baseSeries.getInterval());

        double flip;
        if (extremeType == Extreme.MAX) { flip = 1; } else { flip = -1; }

        Entry<Instant, AssetSnapshot> candidateSnapshotEntry = getEntryFromInstant(baseSeries, seriesStart);
        boolean isExtreme = true;

        while (candidateSnapshotEntry.getKey().isBefore(seriesEnd)) {
            Entry<Instant, AssetSnapshot> ceilingRadiusSnapshotEntry = candidateSnapshotEntry;
            Entry<Instant, AssetSnapshot> floorRadiusSnapshotEntry = candidateSnapshotEntry;

            for (int i = 0; i < radius; i++) {
                try {
                    if (candidateSnapshotEntry.getValue().getAverage() * flip >= flip * ceilingRadiusSnapshotEntry.getValue().getAverage()
                        && candidateSnapshotEntry.getValue().getAverage() * flip >= flip * floorRadiusSnapshotEntry.getValue().getAverage()) {
                        ceilingRadiusSnapshotEntry = baseSeries.higherEntry(ceilingRadiusSnapshotEntry.getKey());
                        floorRadiusSnapshotEntry = baseSeries.lowerEntry(floorRadiusSnapshotEntry.getKey());
                    } else {
                        isExtreme = false;
                    }
                } catch (NullPointerException ignored) {}
            }

            if (isExtreme) {
                extremes.put(candidateSnapshotEntry.getKey(), candidateSnapshotEntry.getValue());
            }

            try {
                candidateSnapshotEntry = baseSeries.higherEntry(ceilingRadiusSnapshotEntry.getKey());
            } catch (NullPointerException e) {
                break;
            }
        }
        return extremes;
    }

    Entry<Instant, AssetSnapshot> getEntryFromInstant(SnapShotSeries snapshots, Instant instant) {
        for (Entry<Instant, AssetSnapshot> entry : snapshots.entrySet()) {
            if (entry.getKey().equals(instant)) {
                return entry;
            }
        }
        return null;
    }

    enum Extreme {
        MIN,
        MAX
    }
}

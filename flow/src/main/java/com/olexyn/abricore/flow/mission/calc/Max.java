package com.olexyn.abricore.flow.mission.calc;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.time.Instant;
import java.util.Map.Entry;

public class Max {

    public static void calcGlobalMax(String... args) {
        Asset asset = AssetService.ofName("XAGUSD");
        Series treeMap = SeriesService.of(asset);

        ANum max = new ANum(0,0);



        for (Entry<Instant, AssetSnapshot> entry : treeMap.entrySet()) {
            if (entry.getValue().getPrice().getTraded().greater(max)) {
                max = entry.getValue().getPrice().getTraded();
            }
        }


    }

    public Series calcMaximas(Series baseSeries, int radius, Instant seriesStart, Instant seriesEnd) {
        return calc( baseSeries,  radius,  seriesStart,  seriesEnd,  Extreme.MAX);
    }

    public Series calcMinimas(Series baseSeries, int radius, Instant seriesStart, Instant seriesEnd) {
        return calc( baseSeries,  radius,  seriesStart,  seriesEnd,  Extreme.MIN);
    }


    private Series calc(Series baseSeries, int radius, Instant seriesStart, Instant seriesEnd, Extreme extremeType) {

        Series extremes = new Series(baseSeries.getAsset());

        ANum flip;
        if (extremeType == Extreme.MAX) { flip = new ANum(1,0); } else { flip = new ANum(-1,0); }

        Entry<Instant, AssetSnapshot> candidateSnapshotEntry = getEntryFromInstant(baseSeries, seriesStart);
        boolean isExtreme = true;

        while (candidateSnapshotEntry.getKey().isBefore(seriesEnd)) {
            Entry<Instant, AssetSnapshot> ceilingRadiusSnapshotEntry = candidateSnapshotEntry;
            Entry<Instant, AssetSnapshot> floorRadiusSnapshotEntry = candidateSnapshotEntry;

            for (int i = 0; i < radius; i++) {
                try {


                    if (candidateSnapshotEntry.getValue().getPrice().getTraded().times(flip).geq(ceilingRadiusSnapshotEntry.getValue().getPrice().getTraded().times(flip))
                        && candidateSnapshotEntry.getValue().getPrice().getTraded().times(flip).geq(floorRadiusSnapshotEntry.getValue().getPrice().getTraded().times(flip))) {
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

    Entry<Instant, AssetSnapshot> getEntryFromInstant(Series snapshots, Instant instant) {
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

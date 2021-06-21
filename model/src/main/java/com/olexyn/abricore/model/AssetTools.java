package com.olexyn.abricore.model;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.time.Duration;

public class AssetTools {

    private final Asset asset;
    private final AssetSnapshot snapshot;
    private final Series series;

    public AssetTools(AssetSnapshot snapshot) {
        this.snapshot = snapshot;
        this.asset = snapshot.getAsset();
        this.series = snapshot.getSeries();
    }


    public ANum getMa(Duration duration) {

        AssetSnapshot before = series.getFirstSnapBefore(snapshot);

        while (before.getInstant().plus(duration).isAfter(snapshot.getInstant())) {
            before.getPrice().getTraded();
            Duration.between(before.getInstant(), snapshot.getInstant());
        }
        return null;
    }
}

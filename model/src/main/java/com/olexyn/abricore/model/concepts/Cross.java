package com.olexyn.abricore.model.concepts;


import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.Interval;

import java.time.Instant;
import java.util.Map.Entry;

public class Cross {

    Asset risingAsset;
    Asset fallingAsset;
    Instant crossingInstant;

    /**
     * Represents two asset quotes crossing on the chart.
     * @param asset1
     * @param asset2
     * @param interval
     * @param instant
     */
    Cross(Asset asset1, Asset asset2, Interval interval, Instant instant) {

        Entry<Instant, AssetSnapshot> asset1Next = asset1.getSnapShots(interval).higherEntry(instant);
        Entry<Instant,AssetSnapshot> asset1Prev= asset1.getSnapShots(interval).lowerEntry(instant);

        Entry<Instant,AssetSnapshot> asset2Next = asset2.getSnapShots(interval).higherEntry(instant);
        Entry<Instant,AssetSnapshot> asset2Prev= asset2.getSnapShots(interval).lowerEntry(instant);

        if (asset1Next.getValue().getPrice() > asset2Next.getValue().getPrice()
        && asset1Prev.getValue().getPrice() < asset2Prev.getValue().getPrice()) {
            this.risingAsset = asset1;
            this.fallingAsset = asset2;
            this.crossingInstant = instant;
        }
    }
}




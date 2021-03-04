package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Indicator;
import com.olexyn.abricore.model.snapshots.IndicatorRange;

import java.time.Instant;
import java.util.Map.Entry;

public class CalcCorsses {


    void getCorsses(Asset asset, Indicator indicator, IndicatorRange a, IndicatorRange b, Instant startLimit, Instant endLimit) {

    }


    Asset risingAsset;
    Asset fallingAsset;
    Instant crossingInstant;

    /**
     * Represents two asset quotes crossing on the chart.

     */
   // void foo (Asset asset1, Indicator indicator, IndicatorRange a, IndicatorRange b, Interval interval, Instant instant) {
   //
   //     Entry<Instant, AssetSnapshot> asset1Next = asset1.getSnapShots(interval).higherEntry(instant);
   //     Entry<Instant,AssetSnapshot> asset1Prev= asset1.getSnapShots(interval).lowerEntry(instant);
   //
   //     Entry<Instant,AssetSnapshot> asset2Next = asset2.getSnapShots(interval).higherEntry(instant);
   //     Entry<Instant,AssetSnapshot> asset2Prev= asset2.getSnapShots(interval).lowerEntry(instant);
   //
   //     if (asset1Next.getValue().getAverage() > asset2Next.getValue().getAverage()
   //         && asset1Prev.getValue().getAverage() < asset2Prev.getValue().getAverage()) {
   //         this.risingAsset = asset1;
   //         this.fallingAsset = asset2;
   //         this.crossingInstant = instant;
   //     }
   // }
}

package com.olexyn.abricore.model.concepts.barriers;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.Stock;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class manages a TreeMap with notable price-levels (barriers) for an asset. <br>
 * To do so it also contains utility methods. <br>
 * The typings of the TreeMap are (AssetSnapshot, Confidence). <br>
 * Confidence tracks a balanced estimation of the probability of the effectiveness of the barrier. <br>
 * The price-levels themselves are represented by AssetSnapshot. This way they can be filtered by time.
 */
public abstract class Barriers {

    private BarrierType barrierType;

    Asset asset;

    TreeMap<AssetSnapshot, Double> barriers = new TreeMap<>();


    Integer confidenceSum = 0;


    public void put( AssetSnapshot value, Integer confidence) {
        Integer oldConfidenceSum = confidenceSum;
        confidenceSum += confidence;
        for (Entry<AssetSnapshot,Double> entry : barriers.entrySet()) {
            entry.setValue( entry.getValue() * oldConfidenceSum / confidenceSum);
        }
        barriers.put(value, confidence.doubleValue() / confidenceSum);
    }


    public void put (AssetSnapshot value) {
        put(value, 1);
    }

    /**
     * Clusters barriers that are withing <b>resolution</b> of each other.
     */
    public void cluster( Double resolution) {
        Double distance = resolution * range();
        for (Entry<AssetSnapshot,Double> entry : barriers.entrySet()) {
            AssetSnapshot lowerBarrier = entry.getKey();
            AssetSnapshot higherBarrier = barriers.higherKey(entry.getKey());
            if (lowerBarrier.getPrice() + distance > higherBarrier.getPrice()) {
                AssetSnapshot clusteredBarrier = merge(lowerBarrier, higherBarrier);

                Double clusteredConfidence = barriers.get(lowerBarrier) + barriers.get(higherBarrier);
                barriers.remove(lowerBarrier);
                barriers.remove(higherBarrier);
                barriers.put(clusteredBarrier, clusteredConfidence);
            }
        }
    }

    /**
     * TODO transform COnfidences to be meaningful
     * @param instant
     */
    public TreeMap<AssetSnapshot,Double> filterByTime ( Instant instant) {
        TreeMap<AssetSnapshot, Double> out = new TreeMap<>();
        for (Entry<AssetSnapshot,Double> entry : barriers.entrySet()) {
            if (entry.getKey().getInstant().isAfter(instant)) {
                out.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

    private AssetSnapshot merge(AssetSnapshot snapshot1, AssetSnapshot snapshot2) {
        AssetSnapshot out = new AssetSnapshot(new Stock("test"), Interval.M_30);
        out.setPrice((snapshot1.getPrice() + snapshot2.getPrice()) / 2);
        out.setInstant(snapshot1.getInstant().isBefore(snapshot2.getInstant()) ? snapshot2.getInstant() : snapshot1.getInstant());
        return out;
    }

    private Long range() {
        return barriers.lastKey().getPrice() - barriers.firstKey().getPrice();
    }

    abstract BarrierType getBarrierType();
}

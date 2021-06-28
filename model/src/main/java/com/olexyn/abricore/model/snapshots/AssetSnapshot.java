package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.ANum;

import java.time.Instant;

/**
 *
 */
public class AssetSnapshot {

    private Series series;
    private final Asset asset;

    private Instant instant;
    private Price price = new Price();
    private ANum volume;
    private ANum range;

    public AssetSnapshot(Asset asset) {
        this.asset = asset;

    }

    // GETTERS / SETTERS

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Asset getAsset() {
        return asset;
    }


    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public ANum getVolume() {
        return volume;
    }

    public void setVolume(ANum volume) {
        this.volume = volume;
    }

    public ANum getRange() {
        return range;
    }

    public void setRange(ANum range) {
        this.range = range;
    }

    public void mergeFrom(AssetSnapshot newSnap) {
        boolean sameAsset = this.getAsset().equals(newSnap.getAsset());
        boolean sameInstant = this.getInstant().equals(newSnap.getInstant());
        if (!sameAsset || !sameInstant) {
            return;
        }
        this.getPrice().mergeFrom(newSnap.getPrice());
        if (this.volume == null) {
            this.volume = newSnap.getVolume();
        }
        if (this.range == null) {
            this.range = newSnap.getRange();
        }
    }



}

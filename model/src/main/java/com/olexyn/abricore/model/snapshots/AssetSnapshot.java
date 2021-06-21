package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.ANum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.util.Constants.COMMA;
import static com.olexyn.abricore.util.Constants.NEWLINE;

/**
 *
 */
public class AssetSnapshot {

    private SnapShotSeries series;
    private final Asset asset;

    private Instant instant;
    private Price price = new Price();
    private ANum volume;

    public AssetSnapshot(Asset asset) {
        this.asset = asset;

    }

    // GETTERS / SETTERS

    public SnapShotSeries getSeries() {
        return series;
    }

    public void setSeries(SnapShotSeries series) {
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



}

package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;

import java.time.Instant;

/**
 *
 */
public abstract class AssetSnapshot implements Comparable{

    private Asset asset;

    private Instant instant;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Interval interval;

    private double price;

    public Asset getAsset() {
        return asset;
    }

    public AssetSnapshot(Asset asset, Interval interval) {
        this.asset =asset;
        this.interval = interval;
    }

    public Instant getInstant() {
        return instant;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int compareTo(Object o) {
        // TODO
        return 0;
    }


    private void updatePrice() {
        boolean onz = open != null;
        boolean cnz = close != null;
        boolean lnz = low != null;
        boolean hnz = high != null;
        if (onz && cnz) {
            price = (open + close) / 2;
        } else if (onz) {
            price = open;
        } else if (cnz) {
            price = close;
        }
    }


    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
        updatePrice();
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
        updatePrice();
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
        updatePrice();
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
        updatePrice();
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }


    public void assign(String[] columnOrder, int i, String[] lineArray) {
        String candidate = columnOrder[i].toUpperCase().trim();
        if (candidate.equals("TIME")) {
            setInstant(Instant.ofEpochSecond(Long.parseLong(lineArray[i])));
        } else if (candidate.equals("OPEN")){
            setOpen(Double.parseDouble(lineArray[i]));
        } else if (candidate.equals("HIGH")){
            setHigh(Double.parseDouble(lineArray[i]));
        } else if (candidate.equals("LOW")){
            setLow(Double.parseDouble(lineArray[i]));
        } else if (candidate.equals("CLOSE")){
            setClose(Double.parseDouble(lineArray[i]));
        } else if (candidate.equals("VOLUME")){
            setVolume(Double.parseDouble(lineArray[i]));
        }
    }
}

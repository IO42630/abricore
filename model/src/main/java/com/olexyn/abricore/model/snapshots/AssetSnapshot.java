package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.calc.Calc;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;

import java.time.Instant;

/**
 *
 */
public class AssetSnapshot implements Comparable{

    private Asset asset;

    private Instant instant;
    private Long open;
    private Long high;
    private Long low;
    private Long close;
    private Long volume;
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


    public Long getOpen() {
        return open;
    }

    public void setOpen(Long open) {
        this.open = open;
        updatePrice();
    }

    public Long getHigh() {
        return high;
    }

    public void setHigh(Long high) {
        this.high = high;
        updatePrice();
    }

    public Long getLow() {
        return low;
    }

    public void setLow(Long low) {
        this.low = low;
        updatePrice();
    }

    public Long getClose() {
        return close;
    }

    public void setClose(Long close) {
        this.close = close;
        updatePrice();
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }


    public void assign(String[] columnOrder, int i, String[] lineArray) {
        String candidate = columnOrder[i].toUpperCase().trim();
        if (candidate.equals("TIME")) {
            setInstant(Instant.ofEpochSecond(Long.parseLong(lineArray[i])));
        } else if (candidate.equals("OPEN")){
            setOpen(Calc.parseLong(lineArray[i]));
        } else if (candidate.equals("HIGH")){
            setHigh(Calc.parseLong(lineArray[i]));
        } else if (candidate.equals("LOW")){
            setLow(Calc.parseLong(lineArray[i]));
        } else if (candidate.equals("CLOSE")){
            setClose(Calc.parseLong(lineArray[i]));
        } else if (candidate.equals("VOLUME")){
            setVolume(Calc.parseLong(lineArray[i]));
        }
    }
}

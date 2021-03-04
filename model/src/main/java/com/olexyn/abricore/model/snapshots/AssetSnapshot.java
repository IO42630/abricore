package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.calc.Calc;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;

import static com.olexyn.abricore.model.snapshots.IndicatorRange.*;

import java.time.Instant;

/**
 *
 */
public class AssetSnapshot implements Comparable<AssetSnapshot>{

    private final Asset asset;
    private final Interval interval;

    // mined fields
    private Instant instant;
    private Long open;
    private Long high;
    private Long low;
    private Long close;
    private Long volume;

    // locally calculated fields
    private Long average;

    // externally calculated fields
    private Indicator ma;
    private Indicator lowBol;
    private Indicator highBol;

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




    public Long getOpen() {
        return open;
    }

    public void setOpen(Long open) {
        this.open = open;
    }

    public Long getHigh() {
        return high;
    }

    public void setHigh(Long high) {
        this.high = high;
    }

    public Long getLow() {
        return low;
    }

    public void setLow(Long low) {
        this.low = low;
    }

    public Long getClose() {
        return close;
    }

    public void setClose(Long close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Indicator getMa() {
        return ma;
    }

    public void setMa(Indicator ma) {
        this.ma = ma;
    }

    public Indicator getLowBol() {
        return lowBol;
    }

    public void setLowBol(Indicator lowBol) {
        this.lowBol = lowBol;
    }

    public Indicator getHighBol() {
        return highBol;
    }

    public void setHighBol(Indicator highBol) {
        this.highBol = highBol;
    }


    public Long getAverage() {
        if (average != null) {
            return average;
        }
        if (low != null && high !=null && open != null && close != null) {
            average = (low + high + open + close) / 4;
        }
        return average;
    }



    public static void loadData(AssetSnapshot snapshot, String[] headerArray, String[] lineArray) {

        for (int i = 0; i < headerArray.length; i++) {
            switch (headerArray[i].toUpperCase().trim()) {
                case "TIME":
                    snapshot.setInstant(Instant.ofEpochSecond(Long.parseLong(lineArray[i])));
                    break;
                case "OPEN":
                    snapshot.setOpen(Calc.parseLong(lineArray[i]));
                    break;
                case "HIGH":
                    snapshot.setHigh(Calc.parseLong(lineArray[i]));
                    break;
                case "LOW":
                    snapshot.setLow(Calc.parseLong(lineArray[i]));
                    break;
                case "CLOSE":
                    snapshot.setClose(Calc.parseLong(lineArray[i]));
                    break;
                case "VOLUME":
                    snapshot.setVolume(Calc.parseLong(lineArray[i]));
                    break;
                case "MA5":
                    snapshot.getMa().set(R5, Calc.parseLong(lineArray[i]));
                    break;
                case "MA10":
                    snapshot.getMa().set(R10, Calc.parseLong(lineArray[i]));
                    break;
                case "MA20":
                    snapshot.getMa().set(R20, Calc.parseLong(lineArray[i]));
                    break;
                case "MA50":
                    snapshot.getMa().set(R50, Calc.parseLong(lineArray[i]));
                    break;
                case "MA100":
                    snapshot.getMa().set(R100, Calc.parseLong(lineArray[i]));
                    break;
                case "MA200":
                    snapshot.getMa().set(R200, Calc.parseLong(lineArray[i]));
                    break;
            }
        }
    }

    @Override
    public int compareTo(AssetSnapshot assetSnapshot) {
        return 0;
    }
}

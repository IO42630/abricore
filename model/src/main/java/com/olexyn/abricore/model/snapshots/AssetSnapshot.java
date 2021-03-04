package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.util.Calc;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;

import static com.olexyn.abricore.model.snapshots.IndicatorRange.*;

import static com.olexyn.abricore.util.Constants.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AssetSnapshot {

    private SnapShotSeries series;

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
    private Indicator ma = new Indicator();
    private Indicator lowBol = new Indicator();
    private Indicator highBol = new Indicator();

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

    public SnapShotSeries getSeries() {
        return series;
    }

    public void setSeries(SnapShotSeries series) {
        this.series = series;
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

    public void update(AssetSnapshot otherSnapshot) {
        for (IndicatorRange range : IndicatorRange.values()) {
            if (this.getMa().get(range) == null) {
                this.getMa().set(range, otherSnapshot.getMa().get(range));
            }
        }
    }


    public String getHeader() {
        return "TIME,OPEN,HIGH,LOW,CLOSE,VOLUME,MA5,MA10,MA20,MA50,MA100,MA200\n";
    }

    public void buildLine(StringBuilder lineBuilder) {
        List<Long> values = new ArrayList<>();
        values.add(getOpen());
        values.add(getHigh());
        values.add(getLow());
        values.add(getClose());
        values.add(getVolume());
        values.add(getMa().get(R5));
        values.add(getMa().get(R10));
        values.add(getMa().get(R20));
        values.add(getMa().get(R50));
        values.add(getMa().get(R100));
        values.add(getMa().get(R200));

        lineBuilder.append(instant.toEpochMilli() / 1000).append(COMMA);
        for (int i = 0 ; i< values.size(); i++) {
            lineBuilder.append(Calc.parseString(values.get(i)));
            if (i + 1 < values.size()) {
                lineBuilder.append(COMMA);
            } else {
                lineBuilder.append(NEWLINE);
            }
        }
    }


    public void updateForced(AssetSnapshot otherSnapshot) {
        for (IndicatorRange range : IndicatorRange.values()) {
            this.getMa().set(range, otherSnapshot.getMa().get(range));
        }
    }
}

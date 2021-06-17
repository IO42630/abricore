package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.util.Calc;
import com.olexyn.abricore.util.enums.Currency;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.model.snapshots.RangeEnum.R10;
import static com.olexyn.abricore.model.snapshots.RangeEnum.R100;
import static com.olexyn.abricore.model.snapshots.RangeEnum.R20;
import static com.olexyn.abricore.model.snapshots.RangeEnum.R200;
import static com.olexyn.abricore.model.snapshots.RangeEnum.R5;
import static com.olexyn.abricore.model.snapshots.RangeEnum.R50;
import static com.olexyn.abricore.util.Constants.COMMA;
import static com.olexyn.abricore.util.Constants.NEWLINE;

/**
 *
 */
public class AssetSnapshot {

    private SnapShotSeries series;
    private List<SnapShotType> typeList = new ArrayList<>();

    private final Asset asset;
    private final Interval interval;
    private Currency currency;

    // GENERIC : collected data
    private boolean isMarketOpen;
    private Instant instant;

    // BAR : collected data

    private Long open;
    private Long high;
    private Long low;
    private Long close;
    private Long volume;

    // SPREAD : collected data
    private Long bidVol;
    private Long askVol;
    private Double bidPrice;
    private Double askPrice;

    // OPTION : collected data
    private Long multiplier;
    private Double strike;
    private LocalDateTime expiry;


    // locally calculated fields
    private Long average;

    // externally calculated fields
    private Indicator ma = new Indicator();
    private Indicator lowBol = new Indicator();
    private Indicator highBol = new Indicator();

    public AssetSnapshot(Asset asset, Interval interval) {
        this.asset =asset;
        this.interval = interval;
    }

    // GETTERS / SETTERS

    public SnapShotSeries getSeries() {
        return series;
    }

    public void setSeries(SnapShotSeries series) {
        this.series = series;
    }

    public List<SnapShotType> getTypeList() {
        return typeList;
    }

    public Asset getAsset() {
        return asset;
    }

    public Interval getInterval() {
        return interval;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isMarketOpen() {
        return isMarketOpen;
    }

    public boolean isMarketClosed() {
        return !isMarketOpen;
    }

    public void setIsMarketOpen(Boolean open) {
        isMarketOpen = open;
    }

    public Instant getInstant() {
        return instant;
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

    // SPREAD : getters /setters

    public Long getBidVol() {
        return bidVol;
    }

    public void setBidVol(Long bidVol) {
        this.bidVol = bidVol;
    }

    public Long getAskVol() {
        return askVol;
    }

    public void setAskVol(Long askVol) {
        this.askVol = askVol;
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(Double askPrice) {
        this.askPrice = askPrice;
    }

    // OPTION : getters / setters

    public Long getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Long multiplier) {
        this.multiplier = multiplier;
    }

    public Double getStrike() {
        return strike;
    }

    public void setStrike(Double strike) {
        this.strike = strike;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
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



    public static void mapData(AssetSnapshot snapshot, String[] headerArray, String[] lineArray) {

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
        for (RangeEnum range : RangeEnum.values()) {
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
        for (RangeEnum range : RangeEnum.values()) {
            this.getMa().set(range, otherSnapshot.getMa().get(range));
        }
    }

    //TODO complete
    public AssetSnapshot copy(SnapShotSeries otherSeries) {

        AssetSnapshot copy = new AssetSnapshot(asset,interval);
        if (otherSeries != null) {
            copy.setSeries(otherSeries);
        }
        copy.setOpen(open);
        copy.setClose(close);
        return copy;
    }


}

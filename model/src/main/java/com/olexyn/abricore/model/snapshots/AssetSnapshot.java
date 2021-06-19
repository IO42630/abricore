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

    public static void mapData(AssetSnapshot snapshot, String[] headerArray, String[] lineArray) {

        for (int i = 0; i < headerArray.length; i++) {
            switch (headerArray[i].toUpperCase().trim()) {
                case "TIME":
                    snapshot.setInstant(Instant.parse(lineArray[i]));
                    break;
                case "PRICE_TRADED":
                    snapshot.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case "PRICE_BID":
                    snapshot.getPrice().setBid(ANum.of(lineArray[i]));
                    break;
                case "PRICE_ASK":
                    snapshot.getPrice().setAsk(ANum.of(lineArray[i]));
                    break;
                case "VOLUME":
                    snapshot.setVolume(ANum.of(lineArray[i]));
                    break;
                default:
                    break;
            }
        }
    }

    public void buildLine(StringBuilder lineBuilder) {
        List<ANum> values = new ArrayList<>();
        values.add(getPrice().getTraded());
        values.add(getPrice().getBid());
        values.add(getPrice().getAsk());
        values.add(getVolume());

        lineBuilder.append(instant.toString()).append(COMMA);
        for (int i = 0 ; i< values.size(); i++) {
            lineBuilder.append(values.get(i).toString());
            if (i + 1 < values.size()) {
                lineBuilder.append(COMMA);
            } else {
                lineBuilder.append(NEWLINE);
            }
        }
    }

}

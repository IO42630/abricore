package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.enums.Currency;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;

public class SqQuoteSnapshot {

    private SqQuoteSnapshot() {}

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public ANum getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(ANum lastPrice) {
        this.lastPrice = lastPrice;
    }

    public ANum getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(ANum multiplier) {
        this.multiplier = multiplier;
    }

    public ANum getBidVol() {
        return bidVol;
    }

    public void setBidVol(ANum bidVol) {
        this.bidVol = bidVol;
    }

    public ANum getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(ANum bidPrice) {
        this.bidPrice = bidPrice;
    }

    public ANum getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(ANum askPrice) {
        this.askPrice = askPrice;
    }

    public ANum getAskVol() {
        return askVol;
    }

    public void setAskVol(ANum askVol) {
        this.askVol = askVol;
    }

    public ANum getStrike() {
        return strike;
    }

    public void setStrike(ANum strike) {
        this.strike = strike;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    private Currency currency;
    private ANum lastPrice;
    private ANum multiplier;
    private ANum bidVol;
    private ANum bidPrice;
    private ANum askPrice;
    private ANum askVol;
    private ANum strike;
    private Instant expiry;
    private Instant instant;
    private Asset asset;



    public static SqQuoteSnapshot of(Map<String, String> dataMap, Asset asset) {

        SqQuoteSnapshot output = new SqQuoteSnapshot();
        output.asset = asset;

        for (Entry<String,String> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();

            if (key.startsWith("Letzter Preis")) {
                output.setLastPrice(ANum.of(val));
            }
            if(key.startsWith("Geldkurs-Volumen")) {
                output.setBidVol(ANum.of(val));
            }
            if(key.startsWith("Geldkurs ")) {
                output.setBidPrice(ANum.of(val));
            }
            if (key.startsWith("Strike")) {
                output.setStrike(ANum.of(val.substring(0, val.indexOf(Constants.SPACE))));
            }
            if(key.startsWith("Briefkurs-Volumen")) {
                output.setAskVol(ANum.of(val));
            }
            if(key.startsWith("Briefkurs ")) {
                output.setAskPrice(ANum.of(val));
            }
            if(key.equals("Multiplier")) {
                output.setMultiplier(ANum.of(val));
            }
            if(key.equals("Währung")) {
                output.setCurrency(Currency.valueOf(val));
            }
            if(key.equals("Verfall") && !val.isEmpty()) {
                output.setExpiry(Instant.parse(val));
            }
        }
        LocalDate date = dataMap.keySet().stream()
            .filter(x -> x.startsWith("Basiswert Preis "))
            .map(x -> x.replace("Basiswert Preis ", Constants.EMPTY))
            .map(x -> LocalDate.parse(x, DateTimeFormatter.ofPattern("dd-mm-jjjj")))
            .findFirst().orElseThrow();
        LocalTime time = dataMap.keySet().stream()
            .filter(x -> x.startsWith("Geldkurs "))
            .map(x -> x.replace("Geldkurs ", Constants.EMPTY))
            .map(LocalTime::parse)
            .findFirst().orElseThrow();
        output.setInstant(Instant.from(LocalDateTime.of(date, time)));
        return output;
    }

    public AssetSnapshot toAssetSnapShot() {
        AssetSnapshot snapshot = new AssetSnapshot(asset);
        snapshot.getPrice().setBid(getBidPrice());
        snapshot.getPrice().setAsk(getAskPrice());
        return snapshot;
    }

}

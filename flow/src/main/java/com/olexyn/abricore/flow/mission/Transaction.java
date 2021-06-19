package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.ANum;

import java.time.Instant;

public class Transaction {

    private Asset asset;

    private boolean active = false;

    private Instant buyInstant;
    private Instant sellInstant;
    private ANum amount;
    private ANum buyPrice;
    private ANum sellPrice;
    private ANum buyFee = new ANum(0,0);
    private ANum sellFee = new ANum(0,0);

    private Transaction() {}

    public Transaction (Asset asset, Instant buyInstant, ANum amount, ANum buyPrice) {
        this.asset = asset;
        this.buyInstant = buyInstant;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.active = true;
    }

    public void end(Instant sellInstant, ANum sellPrice) {
        this.sellInstant = sellInstant;
        this.sellPrice = sellPrice;
        this.active = false;
    }


    public Asset getAsset() {
        return asset;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getBuyInstant() {
        return buyInstant;
    }

    public Instant getSellInstant() {
        return sellInstant;
    }

    public ANum getAmount() {
        return amount;
    }

    public ANum getBuyPrice() {
        return buyPrice;
    }

    public ANum getSellPrice() {
        return sellPrice;
    }

    public ANum getBuyFee() {
        return buyFee;
    }

    public void setBuyFee(ANum buyFee) {
        this.buyFee = buyFee;
    }

    public ANum getSellFee() {
        return sellFee;
    }

    public void setSellFee(ANum sellFee) {
        this.sellFee = sellFee;
    }

    public ANum getProfit() {
        if (active) {
            return null;
        }
        return getRevenue().minus(getSize()).minus(buyFee).minus(sellFee);
    }

    public ANum getGain() {
        if (active) {
            return null;
        }
        return getRevenue().div(getSize());
    }

    public ANum getRevenue() {
        if (active) {
            return null;
        }
        return sellPrice.times(amount);
    }

    public ANum getSize() {
        return buyPrice.times(amount);
    }
}



package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.Calc;

import java.time.Instant;

public class Transaction {

    private Asset asset;

    private boolean active = false;

    private Instant buyInstant;
    private Instant sellInstant;
    private Long amount;
    private Long buyPrice;
    private Long sellPrice;
    private Long buyFee = 0L;
    private Long sellFee = 0L;

    private Transaction() {}

    public Transaction (Asset asset, Instant buyInstant, Long amount, Long buyPrice) {
        this.asset = asset;
        this.buyInstant = buyInstant;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.active = true;
    }

    public void end(Instant sellInstant, Long sellPrice) {
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

    public Long getAmount() {
        return amount;
    }

    public Long getBuyPrice() {
        return buyPrice;
    }

    public Long getSellPrice() {
        return sellPrice;
    }

    public Long getBuyFee() {
        return buyFee;
    }

    public void setBuyFee(Long buyFee) {
        this.buyFee = buyFee;
    }

    public Long getSellFee() {
        return sellFee;
    }

    public void setSellFee(Long sellFee) {
        this.sellFee = sellFee;
    }

    public Long getProfit() {
        if (active) {
            return null;
        }
        return  getRevenue() - getSize() - buyFee - sellFee;
    }

    public Long getGain() {
        if (active) {
            return null;
        }
        return getRevenue() / getSize();
    }

    public Long getRevenue() {
        if (active) {
            return null;
        }
        return Calc.multiply(sellPrice, amount);
    }

    public Long getSize() {
        return Calc.multiply(buyPrice, amount);
    }
}



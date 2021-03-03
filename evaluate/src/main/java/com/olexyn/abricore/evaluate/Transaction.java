package com.olexyn.abricore.evaluate;

import java.time.Instant;

public class Transaction {


    private Instant buyInstant;
    private Instant sellInstant;
    private Long amount;
    private Long price;

    public Instant getBuyInstant() {
        return buyInstant;
    }

    public void setBuyInstant(Instant buyInstant) {
        this.buyInstant = buyInstant;
    }

    public Instant getSellInstant() {
        return sellInstant;
    }

    public void setSellInstant(Instant sellInstant) {
        this.sellInstant = sellInstant;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}



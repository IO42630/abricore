package com.olexyn.abricore.model.snapshots;


import com.olexyn.abricore.util.ANum;

import java.util.Objects;

public class Price {

    private ANum traded;
    private ANum bid;
    private ANum ask;

    public ANum getTraded() {
        return traded;
    }

    public void setTraded(ANum traded) {
        this.traded = traded;
    }

    public ANum getBid() {
        return bid;
    }

    public void setBid(ANum bid) {
        this.bid = bid;
    }

    public ANum getAsk() {
        return ask;
    }

    public void setAsk(ANum ask) {
        this.ask = ask;
    }

    public ANum getSpread() {
        return ask.minus(bid);
    }

    public void mergeFrom(Price newPrice) {
        if (newPrice != null) {
            if (this.traded == null) {
                this.traded = newPrice.getTraded();
            } else {
                this.traded.mergeFrom(newPrice.getTraded());
            }
            if (this.bid == null) {
                this.bid = newPrice.getBid();
            } else {
                this.bid.mergeFrom(newPrice.getBid());
            }
            if (this.ask == null) {
                this.ask = newPrice.getAsk();
            } else {
                this.ask.mergeFrom(newPrice.getAsk());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Price price = (Price) o;
        return Objects.equals(traded, price.traded) &&
            Objects.equals(bid, price.bid) &&
            Objects.equals(ask, price.ask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traded, bid, ask);
    }
}

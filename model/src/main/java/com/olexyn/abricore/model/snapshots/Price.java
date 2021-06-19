package com.olexyn.abricore.model.snapshots;


import com.olexyn.abricore.util.ANum;

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
}

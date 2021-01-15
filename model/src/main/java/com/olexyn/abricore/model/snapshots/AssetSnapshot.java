package com.olexyn.abricore.model.snapshots;

import java.time.Instant;

public abstract class AssetSnapshot implements Comparable{

    private Instant instant;

    private double price = 24.6;

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int compareTo(Object o) {
        // TODO
        return 0;
    }
}

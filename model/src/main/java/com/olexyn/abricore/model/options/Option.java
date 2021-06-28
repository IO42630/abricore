package com.olexyn.abricore.model.options;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.ANum;

import java.time.Instant;

public abstract class Option extends Asset {

    private Asset underlying;
    private ANum strike;
    private Instant expiry;
    private ANum ratio;

    private OptionType optionType;



    public Option(String name) {
        super(name);
    }

    public ANum getStrike() {
        return strike;
    }

    public Asset getUnderlying() {
        return underlying;
    }

    public void setUnderlying(Asset underlying) {
        this.underlying = underlying;
    }

    public void setStrike(ANum strike) {
        this.strike = strike;
    }

    public Double calculateVolatilityPA() {
        return 0d;
    }

    public Double getTimeTillExpiry() {
        return  0d;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    public ANum getRatio() {
        return ratio;
    }

    public void setRatio(ANum ratio) {
        this.ratio = ratio;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }
}

package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.util.enums.Currency;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This represents a Sq fullquote table.
 */
public class SqSnapshot {

    private Boolean isOpen;
    private Currency currency;
    private Long multiplier;
    private Long bidVol;
    private Long askVol;
    private Double bidPrice;
    private Double askPrice;
    private Double strike;
    private LocalDateTime expiry;
    private LocalDateTime snapTime;
    private String underlyingAsset;

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Long multiplier) {
        this.multiplier = multiplier;
    }

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

    public LocalDateTime getSnapTime() {
        return snapTime;
    }

    public void setSnapTime(LocalDateTime snapTime) {
        this.snapTime = snapTime;
    }

    public String getUnderlyingAsset() {
        return underlyingAsset;
    }

    public void setUnderlyingAsset(String underlyingAsset) {
        this.underlyingAsset = underlyingAsset;
    }
}

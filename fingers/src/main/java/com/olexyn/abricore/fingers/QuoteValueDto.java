package com.olexyn.abricore.fingers;

import java.time.Period;
import java.time.ZonedDateTime;

public class QuoteValueDto {

    private ZonedDateTime time;
    private Period increment;
    private Double open;
    private Double high;
    private Double low;

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public Period getIncrement() {
        return increment;
    }

    public void setIncrement(Period increment) {
        this.increment = increment;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Double getVolumeMA() {
        return volumeMA;
    }

    public void setVolumeMA(Double volumeMA) {
        this.volumeMA = volumeMA;
    }

    public Double getRSI() {
        return RSI;
    }

    public void setRSI(Double RSI) {
        this.RSI = RSI;
    }

    private Double close;
    private Integer volume;
    private Double volumeMA;
    private Double RSI;
}

package com.olexyn.abricore.model.options;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public abstract class Option extends Asset {

    private Asset underlyingAsset;
    private Integer strike;
    private Instant expiry;



    public Option(String name) {
        super(name);
    }

    public double calculatePrice(AssetSnapshot assetSnapshot) {
        double lnSX = Math.log(assetSnapshot.getAverage() / getStrike());
        double sigmaTwo = Math.pow(calculateVolatilityPA(), 2) / 2;
        double trqSigma = getTimeTillExpiry() * (getRiskFreeInterestPA() - underlyingAsset.getDividend() + sigmaTwo);
        double sigmaSqrtT = calculateVolatilityPA() * Math.sqrt(getTimeTillExpiry());
        double d1 = (lnSX + trqSigma) / sigmaSqrtT;

        double d2 = d1 - sigmaSqrtT;

        NormalDistribution normalDistribution = new NormalDistribution();
        double n1 = normalDistribution.cumulativeProbability(d1);
        double n2 = normalDistribution.cumulativeProbability(d2);

        double part5 = Math.exp(-underlyingAsset.getDividend()* getTimeTillExpiry());
        double part6 = assetSnapshot.getAverage()  * part5 * n1;

        double part7 = Math.exp(-getRiskFreeInterestPA()* getTimeTillExpiry());
        double part8 = getStrike() * part7 * n2;

        double price = part6 - part8;
        price = price >=0 ? price : 0;

        return price;
    }

    public double calculateIntrinsicValue(AssetSnapshot assetSnapshot){
        double intrinsicValue = assetSnapshot.getAverage() - getStrike();
        intrinsicValue = intrinsicValue >= 0 ? intrinsicValue : 0;
        return intrinsicValue;
    }

    public double calculateTimeValue(AssetSnapshot assetSnapshot){
        double timeValue = calculatePrice(assetSnapshot) - calculateIntrinsicValue(assetSnapshot);
        timeValue = timeValue >= 0 ? timeValue : 0;
        return timeValue;
    }

    public Integer getStrike() {
        return strike;
    }

    public void setStrike(Integer strike) {
        this.strike = strike;
    }

    public Double calculateVolatilityPA() {
        return 0d;
    }



    public Double getTimeTillExpiry() {
        return  0d;
    }

    public Double getRiskFreeInterestPA() {
        return null;
    }
}

package com.olexyn.abricore;

import com.olexyn.abricore.model.snapshots.OptionSnapshot;
import org.apache.commons.math3.distribution.NormalDistribution;

public class CallCalculator {

    private final OptionSnapshot option;

    public CallCalculator(OptionSnapshot option){

        this.option = option;
    }

    public double calculatePrice() {
        double lnSX = Math.log(option.getAssetPrice() / option.getStrike());
        double sigmaTwo = Math.pow(option.getVolatilityPA(), 2) / 2;
        double trqSigma = option.getTimeTillExpiry() * (option.getRiskFreeInterestPA() - option.getDividendPA() + sigmaTwo);
        double sigmaSqrtT = option.getVolatilityPA() * Math.sqrt(option.getTimeTillExpiry());
        double d1 = (lnSX + trqSigma) / sigmaSqrtT;

        double d2 = d1 - sigmaSqrtT;

        NormalDistribution normalDistribution = new NormalDistribution();
        double n1 = normalDistribution.cumulativeProbability(d1);
        double n2 = normalDistribution.cumulativeProbability(d2);

        double part5 = Math.exp(-option.getDividendPA()* option.getTimeTillExpiry());
        double part6 = option.getAssetPrice() * part5 * n1;

        double part7 = Math.exp(-option.getRiskFreeInterestPA()* option.getTimeTillExpiry());
        double part8 = option.getStrike() * part7 * n2;

        double price = part6 - part8;
        price = price >=0 ? price : 0;

        return price;
    }

    public double calculateIntrinsicValue(){
        double intrinsicValue = option.getAssetPrice() - option.getStrike();
        intrinsicValue = intrinsicValue >= 0 ? intrinsicValue : 0;
        return intrinsicValue;
    }

    public double calculateTimeValue(){
        double timeValue = calculatePrice() - calculateIntrinsicValue();
        timeValue = timeValue >= 0 ? timeValue : 0;
        return timeValue;
    }

}

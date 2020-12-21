package com.olexyn.abricore;

import org.apache.commons.math3.distribution.NormalDistribution;

public class CallCalculator {

    private final OptionDto optionDto;

    public CallCalculator(OptionDto optionDto){

        this.optionDto = optionDto;
    }

    public double calculatePrice() {
        double lnSX = Math.log(optionDto.getS() / optionDto.getX());
        double sigmaTwo = Math.pow(optionDto.getSigma(), 2) / 2;
        double trqSigma = optionDto.getT() * (optionDto.getR() - optionDto.getQ() + sigmaTwo);
        double sigmaSqrtT = optionDto.getSigma() * Math.sqrt(optionDto.getT());
        double d1 = (lnSX + trqSigma) / sigmaSqrtT;

        double d2 = d1 - sigmaSqrtT;

        NormalDistribution normalDistribution = new NormalDistribution();
        double n1 = normalDistribution.cumulativeProbability(d1);
        double n2 = normalDistribution.cumulativeProbability(d2);

        double part5 = Math.exp(-optionDto.getQ()*optionDto.getT());
        double part6 = optionDto.getS() * part5 * n1;

        double part7 = Math.exp(-optionDto.getR()*optionDto.getT());
        double part8 = optionDto.getX() * part7 * n2;

        double price = part6 - part8;
        price = price >=0 ? price : 0;

        return price;
    }

    public double calculateIntrinsicValue(){
        double intrinsicValue = optionDto.getS() - optionDto.getX();
        intrinsicValue = intrinsicValue >= 0 ? intrinsicValue : 0;
        return intrinsicValue;
    }

    public double calculateTimeValue(){
        double timeValue = calculatePrice() - calculateIntrinsicValue();
        timeValue = timeValue >= 0 ? timeValue : 0;
        return timeValue;
    }

}

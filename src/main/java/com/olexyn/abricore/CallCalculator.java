package com.olexyn.abricore;

import org.apache.commons.math3.distribution.NormalDistribution;

public class CallCalculator {

    private double price;

    public CallCalculator(OptionDto optionDto){
        double part1 = Math.log(optionDto.getS0() / optionDto.getX());
        double part2 = Math.pow(optionDto.getSigma(), 2);
        double part3 = optionDto.getT() * (optionDto.getR() - optionDto.getQ() + part2);
        double part4 = optionDto.getSigma() * Math.sqrt(optionDto.getT());
        double d1 = (part1 + part3) / part4;

        double d2 = d1 - part4;

        NormalDistribution normalDistribution = new NormalDistribution();
        double n1 = normalDistribution.cumulativeProbability(d1);
        double n2 = normalDistribution.cumulativeProbability(d2);

        double part5 = Math.exp(-optionDto.getQ()*optionDto.getT());
        double part6 = optionDto.getS0() * part5 * n1;

        double part7 = Math.exp(-optionDto.getR()*optionDto.getT());
        double part8 = optionDto.getX() * part7 * n2;

        this.price = part6 - part8;
    }

    public double getPrice() {
        return price;
    }
}

package com.olexyn.abricore.model.snapshots;

public abstract class OptionSnapshot {


    private double strike = 23;
    private double timeTillExpiry = 0.3;
    private double riskFreeInterestPA = 0;
    private double dividendPA = 0;
    private double volatilityPA = 0.38;




    public double getStrike() {
        return strike;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    /***
     * @return time till expiry in % of year
     */
    public double getTimeTillExpiry() {
        return timeTillExpiry;
    }

    /***
     * @return time till expiry in % of year
     */
    public void setTimeTillExpiry(double timeTillExpiry) {
        this.timeTillExpiry = timeTillExpiry;
    }


    public double getRiskFreeInterestPA() {
        return riskFreeInterestPA;
    }


    public void setRiskFreeInterestPA(double riskFreeInterestPA) {
        this.riskFreeInterestPA = riskFreeInterestPA;
    }

    public double getDividendPA() {
        return dividendPA;
    }

    public void setDividendPA(double dividendPA) {
        this.dividendPA = dividendPA;
    }

    public double getVolatilityPA() {
        return volatilityPA;
    }

    public void setVolatilityPA(double volatilityPA) {
        this.volatilityPA = volatilityPA;
    }
}

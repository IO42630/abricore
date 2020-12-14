package com.olexyn.abricore;

public class OptionDto {

    private double s = 24.6; // asset price
    private double x = 23;
    private double t = 0.3;
    private double r = 0;
    private double q = 0; // dividend yield %p.a.
    private double sigma = 0.38; // volatility %p.a.


    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }

    /**
     * @return strike price
     */
    public double getX() {
        return x;
    }

    /**
     * @param x strike price
     */
    public void setX(double x) {
        this.x = x;
    }

    /***
     * @return time till expiry in % of year
     */
    public double getT() {
        return t;
    }

    /***
     * @return time till expiry in % of year
     */
    public void setT(double t) {
        this.t = t;
    }

    /**
     * @return risk free interest %p.a.
     */
    public double getR() {
        return r;
    }

    /**
     * @param r risk free interest %p.a.
     */
    public void setR(double r) {
        this.r = r;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
}

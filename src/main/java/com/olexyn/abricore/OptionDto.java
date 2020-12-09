package com.olexyn.abricore;

public class OptionDto {

    private double s = 24.6; // asset price
    private double x = 23; // strike
    private double t = 0.3; // time % of year
    private double r = 0; // risk free interest %p.a.
    private double q = 0; // dividend yield %p.a.
    private double sigma = 0.38; // volatility %p.a.


    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public double getR() {
        return r;
    }

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

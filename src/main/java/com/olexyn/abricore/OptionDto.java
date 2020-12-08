package com.olexyn.abricore;

public class OptionDto {

    private double S0 = 24.6; // asset price
    private double X = 23; // strike
    private double t = 0.3; // time % of year
    private double r = 0; // risk free interest %p.a.
    private double q = 0; // dividend yield %p.a.
    private double sigma = 0.38; // volatility %p.a.


    public double getS0() {
        return S0;
    }

    public void setS0(double s0) {
        S0 = s0;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
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

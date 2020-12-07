package com.olexyn.abricore;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    static double S0 = 24.6; // asset price
    static double X = 23; // strike
    static double t = 0.3; // time % of year
    static double r = 0; // risk free interest %p.a.
    static double q = 0; // dividend yield %p.a.
    static double sigma = 0.38; // volatility %p.a.

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        double part1 = Math.log(S0 / X);
        double part2 = Math.pow(sigma, 2);
        double part3 = t * (r - q + part2);
        double part4 = sigma * Math.sqrt(t);
        double d1 = (part1 + part3) / part4;

        double d2 = d1 - part4;

        NormalDistribution normalDistribution = new NormalDistribution();
        double n1 = normalDistribution.cumulativeProbability(d1);
        double n2 = normalDistribution.cumulativeProbability(d2);

        double part5 = Math.exp(-q*t);
        double part6 = S0 * part5 * n1;

        double part7 = Math.exp(-r*t);
        double part8 = X * part7 * n2;

        double C = part6 - part8;

        int br = 0;


        double[] xData = new double[] { 0.0, 1.0, 2.0 };
        double[] yData = new double[] { 2.0, 1.0, 0.0 };

// Create Chart
        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

// Show it
        new SwingWrapper(chart).displayChart();

// Save it
        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);

// or save it in high-res
        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);

    }
}

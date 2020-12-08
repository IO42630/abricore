package com.olexyn.abricore;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        OptionDto optionDto = new OptionDto();
        optionDto.setS0(24.6);
        optionDto.setX(23);
        optionDto.setT(0.3);
        optionDto.setR(0);
        optionDto.setQ(0);
        optionDto.setSigma(0.38);


        int points = 10;
        double delta = 0.1;

        double[] xData = new double[points];
        double[] yData = new double[points];

        double startPoint = optionDto.getS0() + points * delta / 2;

        for(int i = 0 ; i<points; i++){

            xData[i] = startPoint - i * delta;
            if(xData[i] >=0){
                optionDto.setS0(xData[i]);
                yData[i] = new CallCalculator(optionDto).getPrice();
            }else{
                xData[i] = 0;
                yData[i] = 0;
            }
        }



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

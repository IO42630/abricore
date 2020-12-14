package com.olexyn.abricore;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    static double S0 = 24.6; // asset price
    static double X = 23; // strike
    static double t = 33; // time % of year
    static double r = 0; // risk free interest %p.a.
    static double q = 0; // dividend yield %p.a.
    static double sigma = 0.38; // volatility %p.a.

    public static void main(String[] args) throws IOException {

        OptionDto optionDtoLow = new OptionDto();
        optionDtoLow.setS(23);
        optionDtoLow.setX(23);
        optionDtoLow.setT(0.23);
        optionDtoLow.setR(0);
        optionDtoLow.setQ(0);
        optionDtoLow.setSigma(0.38);

        OptionDto optionDtoHigh = new OptionDto();
        optionDtoHigh.setS(23);
        optionDtoHigh.setX(25);
        optionDtoHigh.setT(0.23);
        optionDtoHigh.setR(0);
        optionDtoHigh.setQ(0);
        optionDtoHigh.setSigma(0.38);


        int points = 40;
        double delta = 0.1;

        double[] xData = new double[points];
        double[] yData = new double[points];

        double startPoint = 27;

        for(int i = 0 ; i<points; i++){

            xData[i] = startPoint - i * delta;
            if(xData[i] >=0){
                optionDtoLow.setS(xData[i]);
                double optionLowPrice = new CallCalculator(optionDtoLow).calculatePrice();
                optionDtoHigh.setS(xData[i]);
                double optionHighPrice = new CallCalculator(optionDtoHigh).calculatePrice();

                yData[i] = optionLowPrice / optionHighPrice;
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

        //XYChart exampleChart = new XYChart();
        XYChart chart2 = getChart();
        new SwingWrapper<XYChart>(chart).displayChart();


    }


    public static XYChart getChart() {

        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).title(App.class.getSimpleName()).xAxisTitle("X").yAxisTitle("Y").build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

        // Series
        chart.addSeries("a", new double[] { 0, 3, 5, 7, 9 }, new double[] { -3, 5, 9, 6, 5 });
        chart.addSeries("b", new double[] { 0, 2, 4, 6, 9 }, new double[] { -1, 6, 4, 0, 4 });
        chart.addSeries("c", new double[] { 0, 1, 3, 8, 9 }, new double[] { -2, -1, 1, 0, 1 });

        return chart;
    }
}

package com.olexyn.abricore;

import com.olexyn.abricore.model.snapshots.OptionSnapshot;
import com.olexyn.abricore.model.snapshots.VanillaOptionSnapshot;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws IOException {

        OptionSnapshot optionLow = new VanillaOptionSnapshot();
        optionLow.setAssetPrice(23);
        optionLow.setStrike(23);
        optionLow.setTimeTillExpiry(0.23);
        optionLow.setRiskFreeInterestPA(0);
        optionLow.setDividendPA(0);
        optionLow.setVolatilityPA(0.38);

        OptionSnapshot optionHigh = new VanillaOptionSnapshot();
        optionHigh.setAssetPrice(23);
        optionHigh.setStrike(25);
        optionHigh.setTimeTillExpiry(0.23);
        optionHigh.setRiskFreeInterestPA(0);
        optionHigh.setDividendPA(0);
        optionHigh.setVolatilityPA(0.38);


        int points = 40;
        double delta = 0.1;

        double[] xData = new double[points];
        double[] yData = new double[points];

        double startPoint = 27;

        for(int i = 0 ; i<points; i++){

            xData[i] = startPoint - i * delta;
            if(xData[i] >=0){
                optionLow.setAssetPrice(xData[i]);
                double optionLowPrice = new CallCalculator(optionLow).calculatePrice();
                optionHigh.setAssetPrice(xData[i]);
                double optionHighPrice = new CallCalculator(optionHigh).calculatePrice();

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

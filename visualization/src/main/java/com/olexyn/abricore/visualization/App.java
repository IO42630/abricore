package com.olexyn.abricore.visualization;

import com.olexyn.abricore.calc.Calc;
import com.olexyn.abricore.model.snapshots.OptionSnapshot;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collector;

/**
 * Hello world!
 */
public class App {


    public static void main(String[] args) throws IOException {

        OptionSnapshot optionLow = new OptionSnapshot();
        optionLow.setStrike(23);
        optionLow.setTimeTillExpiry(0.23);
        optionLow.setRiskFreeInterestPA(0);
        optionLow.setDividendPA(0);
        optionLow.setVolatilityPA(0.38);

        OptionSnapshot optionHigh = new OptionSnapshot();
        optionHigh.setPrice(23L);
        optionHigh.setStrike(25);
        optionHigh.setTimeTillExpiry(0.23);
        optionHigh.setRiskFreeInterestPA(0);
        optionHigh.setDividendPA(0);
        optionHigh.setVolatilityPA(0.38);


        int points = 40;
        Long delta = 100L;

        Long[] xData = new Long[points];
        Long[] yData = new Long[points];

        Long startPoint = 27L;

        for (int i = 0; i < points; i++) {

            xData[i] = startPoint - i * delta;
            if (xData[i] >= 0) {
                optionLow.setPrice(xData[i]);
                Long optionLowPrice = 0L;
                optionHigh.setPrice(xData[i]);
                Long optionHighPrice = 0L;

                yData[i] = optionLowPrice / optionHighPrice;
            } else {
                xData[i] = 0L;
                yData[i] = 0L;
            }
        }



        // Create Chart
        double[] xxData = Arrays.stream(xData).mapToDouble(x -> Double.parseDouble(Calc.parseString(x))).toArray();
        double[] yyData = Arrays.stream(yData).mapToDouble(x -> Double.parseDouble(Calc.parseString(x))).toArray();

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xxData, yyData);





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
        chart.addSeries("a", new double[]{0, 3, 5, 7, 9}, new double[]{-3, 5, 9, 6, 5});
        chart.addSeries("b", new double[]{0, 2, 4, 6, 9}, new double[]{-1, 6, 4, 0, 4});
        chart.addSeries("c", new double[]{0, 1, 3, 8, 9}, new double[]{-2, -1, 1, 0, 1});

        return chart;
    }
}




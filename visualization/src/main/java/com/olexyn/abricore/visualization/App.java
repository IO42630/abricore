package com.olexyn.abricore.visualization;

import com.olexyn.abricore.util.ANum;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {


    public static void main(String[] args) throws IOException {




        int points = 40;
        ANum delta = new ANum(0,0);

        ANum[] xData = new ANum[points];
        ANum[] yData = new ANum[points];

        ANum startPoint = new ANum(27,0);

        for (int i = 0; i < points; i++) {

            // xData[i] = startPoint - i * delta;
            // if (xData[i] >= 0) {
            //     ANum optionLowPrice = new ANum(0,0);
            //     ANum optionHighPrice = new ANum(0,0);
            //
            //     yData[i] = optionLowPrice / optionHighPrice;
            // } else {
            //     xData[i] = new ANum(0,0);
            //     yData[i] = new ANum(0,0);
            // }
        }



        // Create Chart
        // double[] xxData = Arrays.stream(xData).mapToDouble(x -> Double.parseDouble(ANum.toString(x))).toArray();
        // double[] yyData = Arrays.stream(yData).mapToDouble(x -> Double.parseDouble(ANum.toString(x))).toArray();
        //
        // XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xxData, yyData);





        // Show it
        // new SwingWrapper(chart).displayChart();

        // Save it
        // BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);

        // or save it in high-res
        // BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);

        //XYChart exampleChart = new XYChart();
        XYChart chart2 = getChart();
        // new SwingWrapper<>(chart).displayChart();


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




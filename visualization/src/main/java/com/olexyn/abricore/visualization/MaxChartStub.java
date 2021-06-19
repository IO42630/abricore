package com.olexyn.abricore.visualization;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;

import java.util.ArrayList;
import java.util.List;

public class MaxChartStub {

    public static void main(String... args){
        XYChart chart2 = getChart();
        new SwingWrapper<>(chart2).displayChart();
    }

    public static XYChart getChart() {

        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).title(App.class.getSimpleName()).xAxisTitle("X").yAxisTitle("Y").build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

        // Series
        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();
        for (int i=0; i< 800; i++) {
            xList.add((double) i);
            yList.add(Math.random());
        }
        double[] xArray = xList.stream().mapToDouble(x -> x).toArray();
        double[] yArray = yList.stream().mapToDouble(x -> x).toArray();

        chart.addSeries("a", xArray, yArray);
        //chart.addSeries("b", new double[]{0, 2, 4, 6, 9}, new double[]{-1, 6, 4, 0, 4});
        //chart.addSeries("c", new double[]{0, 1, 3, 8, 9}, new double[]{-2, -1, 1, 0, 1});

        return chart;
    }
}

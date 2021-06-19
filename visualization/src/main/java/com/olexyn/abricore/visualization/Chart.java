package com.olexyn.abricore.visualization;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.GetFromSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import com.olexyn.abricore.util.ANum;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

public class Chart {


    public static XYChart makeChart(SnapShotSeries series, Instant from, Instant to, GetFromSnapshot getFromSnapshot) {
       return makeChart(series, from, to, 100, getFromSnapshot);
    }

    public static XYChart makeChart(
        SnapShotSeries series,
        Instant from,
        Instant to,
        int resolution,
        GetFromSnapshot getFromSnapshot
    ) {

        series = series.limitSeries(from, to);
        if (series.size() > resolution) {
            return makeMergedChart(series, from, to, resolution, getFromSnapshot);
        } else {
            return makeDefaultChart(series, from, to, getFromSnapshot);
        }
    }


    private static  XYChart makeMergedChart(
        SnapShotSeries series,
        Instant from,
        Instant to,
        int resolution,
        GetFromSnapshot getFromSnapshot
    ) {



        int mergeSize = series.size() / resolution;
        List<ANum> xList = new ArrayList<>();
        List<ANum> yList = new ArrayList<>();

        // int count = 0;
        // ANum amount = new ANum(0,0);
        // long xCount = 1L;
        // for (Entry<Instant, AssetSnapshot> entry: series.entrySet()) {
        //
        //     if (count == 0) {
        //         count++;
        //     } else if ( count < mergeSize) {
        //         count++;
        //     } else {
        //         xList.add(xCount);
        //         xCount = xCount + 1;
        //         // yList.add(amount/mergeSize);
        //         count = 0;
        //     }
        // }
        // long[] xData = xList.stream().mapToANum(x -> x).toArray();
        // long[] yData = yList.stream().mapToANum(x -> x).toArray();
        //
        // String title = makeTitle(series, from, to);
        // return makeXYChart(title, xData, yData);
        return null;
    }

    private static  XYChart makeDefaultChart(
        SnapShotSeries series,
        Instant from,
        Instant to,
        GetFromSnapshot getFromSnapshot
    ) {
        // List<ANum> xList = new ArrayList<>();
        // List<ANum> yList = new ArrayList<>();
        // long xCount = 1L;
        // for (Entry<Instant, AssetSnapshot> entry: series.entrySet()) {
        //     xList.add(xCount);
        //     xCount = xCount + 1;
        // }
        // long[] xData = xList.stream().mapToANum(x -> x).toArray();
        // long[] yData = yList.stream().mapToANum(x -> x).toArray();
        //
        // String title = makeTitle(series, from, to);
        // return makeXYChart(title, xData, yData);
        return null;
    }

    private static String makeTitle(SnapShotSeries series, Instant from, Instant to) {
        String title = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm", Locale.US);
        title += series.getAsset().getName() + " ";
        //title += formatter.format(from) + " ";
        //title += formatter.format(to) + " ";
        return title;
    }


    private static XYChart makeXYChart (String title, long[] xData, long[] yData) {
        // double[] xxData = Arrays.stream(xData).mapToDouble(x -> Double.parseDouble(ANum.toString(1000*x))).toArray();
        // double[] yyData = Arrays.stream(yData).mapToDouble(x -> Double.parseDouble(ANum.toString(x))).toArray();
        // return QuickChart.getChart(title, "t", "x", null, xxData, yyData);
        return null;
    }
}

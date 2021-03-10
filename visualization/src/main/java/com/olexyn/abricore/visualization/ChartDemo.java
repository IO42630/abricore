package com.olexyn.abricore.visualization;

import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChartDemo {

    public static void main(String... args){
        displayCloseXAGUSD();
    }

    public static void displayCloseXAGUSD() {

        Asset asset = Symbols.getAsset("XAGUSD");
        SnapShotSeries series = new SnapShotSeries(asset, Interval.H_1); // TODO get from store instead
        XYChart chart2 = Chart.makeChart(series, Instant.now(), Instant.now(), AssetSnapshot::getClose);
        new SwingWrapper<>(chart2).displayChart();
    }
}

package com.olexyn.abricore.visualization;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.time.Duration;
import java.time.Instant;

public class ChartDemo {

    public static void main(String... args){
        displayCloseXAGUSD();
    }

    public static void displayCloseXAGUSD() {

        Asset asset = AssetService.ofName("XAGUSD");
        Duration offsetA = Duration.ofDays(60);
        Duration offsetB = Duration.ofDays(58);
        // XYChart chart2 = Chart.makeChart(series, Instant.now().minus(offsetA), Instant.now().minus(offsetB), AssetSnapshot::getClose);
        // new SwingWrapper<>(chart2).displayChart();
        return;
    }
}

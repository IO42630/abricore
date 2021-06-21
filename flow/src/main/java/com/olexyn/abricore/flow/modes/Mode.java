package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.flow.Main;
import com.olexyn.abricore.flow.Timer;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Mode {

     protected Timer timer = new Timer();

     public  abstract void run() throws InterruptedException;

     /**
      * Initialize utilities for Login and Navigation. Depends on target Webservice.
      */
     public abstract void start();

     public void sleep() throws InterruptedException {
          timer.start();
          while (!timer.hasPassed(Duration.ofSeconds(Long.parseLong(Main.properties.getProperty("run.time"))))) {
               Thread.sleep(1000L);
          }
     }

     public abstract void stop();

     protected Series underlyingSeries;

     protected final List<Series> cdfSeriesList = new ArrayList<>();



     public List<Series> getCdfSeriesList() {
          return cdfSeriesList;
     }

     /**
      * This is simply a List that can hold Assets. Do specify rules about allowed Assets in child classes.
      */
     public List<Asset> getAssets() {
          return cdfSeriesList.stream().map(Series::getAsset).collect(Collectors.toList());
     }

     /**
      * Protected in Mode, Public in ObserveMode.
      */
     protected void addCdf(Asset asset) {
          cdfSeriesList.add(new Series(asset));
     }

     public abstract void fetchData() throws InterruptedException;

     public void onSeriesUpdate() {

     }

     protected void putData(List<AssetSnapshot> snapshots) {
          for (AssetSnapshot snapshot : snapshots) {
               cdfSeriesList.stream()
                   .filter(x -> x.getAsset().equals(snapshot.getAsset())).findFirst()
                   .ifPresent(series -> series.put(snapshot.getInstant(), snapshot));
          }
     }

}

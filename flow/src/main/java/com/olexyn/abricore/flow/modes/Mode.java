package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.flow.MainApp;
import com.olexyn.abricore.flow.Timer;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.Observer;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.LogUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Mode implements Runnable, Observer {

     private static final Logger LOGGER = LogUtil.get(Mode.class);

     protected Timer timer = new Timer();

     public void sleep(long interval){
          timer.start();
          while (timer.hasNotPassedSeconds(Duration.ofSeconds(Long.parseLong(MainApp.config.getProperty("run.time.seconds"))))) {
               try {
                    Thread.sleep(interval);
               } catch (InterruptedException ignored) {

               }
          }
     }

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

     public abstract void fetchData() throws InterruptedException, IOException;

     public void onSeriesUpdate() {

     }



}

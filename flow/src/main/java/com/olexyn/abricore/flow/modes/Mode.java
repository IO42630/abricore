package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.flow.Timer;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import util.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Mode extends Observer {

     protected Timer timer = new Timer();

     public  abstract void run() throws InterruptedException;

     /**
      * Initialize utilities for Login and Navigation. Depends on target Webservice.
      */
     public  abstract void start();

     public abstract void stop();

     protected SnapShotSeries underlyingSeries;

     protected final List<SnapShotSeries> cdfSeriesList = new ArrayList<>();



     public List<SnapShotSeries> getCdfSeriesList() {
          return cdfSeriesList;
     }

     /**
      * This is simply a List that can hold Assets. Do specify rules about allowed Assets in child classes.
      */
     public List<Asset> getAssets() {
          return cdfSeriesList.stream().map(SnapShotSeries::getAsset).collect(Collectors.toList());
     }

     /**
      * Protected in Mode, Public in ObserveMode.
      */
     protected void addAsset(Asset asset) {
          cdfSeriesList.add(new SnapShotSeries(asset));
     }

     public abstract void fetchData() throws InterruptedException;

}

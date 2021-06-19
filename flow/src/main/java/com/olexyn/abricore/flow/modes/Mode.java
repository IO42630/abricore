package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Mode {

     public  abstract void start();

     public abstract void stop();

     private final List<SnapShotSeries> snapShotSeriesList = new ArrayList<>();



     public List<SnapShotSeries> getSnapShotSeriesList() {
          return snapShotSeriesList;
     }

     public Optional<SnapShotSeries> getSnapShotSeries(Asset asset, Interval interval) {
          return getSnapShotSeriesList().stream()
              .filter(x -> x.getAsset().equals(asset))
              .filter(x -> x.getInterval().equals(interval))
              .findFirst();
     }

     /**
      * This is simply a List that can hold Assets. Do specify rules about allowed Assets in child classes.
      */
     public List<Asset> getAssets() {
          return snapShotSeriesList.stream().map(SnapShotSeries::getAsset).collect(Collectors.toList());
     }

     public void addAsset(Asset asset) {
          snapShotSeriesList.add(new SnapShotSeries(asset, null));
     }

     public abstract void updateQuote() throws InterruptedException;

}

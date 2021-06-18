package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Mode {

     public  abstract void start();

     public abstract void stop();

     private final List<SnapShotSeries> snapShotSeriesList = new ArrayList<>();



     public List<SnapShotSeries> getSnapShotSeriesList() {
          return snapShotSeriesList;
     }

     public List<Asset> getAssets() {
          return snapShotSeriesList.stream().map(SnapShotSeries::getAsset).collect(Collectors.toList());
     }

     public void addAsset(Asset asset) {
          snapShotSeriesList.add(new SnapShotSeries(asset, null));
     }

}

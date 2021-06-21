package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;

public abstract class ObserveMode extends Mode {

    public ObserveMode(Asset asset) {
        underlyingSeries = SeriesService.of(asset);
    }

    public void addCdf(Asset asset) {
        super.addCdf(asset);
    }



}

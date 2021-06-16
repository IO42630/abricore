package com.olexyn.abricore.model;

import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.HashMap;
import java.util.Map;

public abstract class Asset {


    private final String name;





    public Asset(String name) {
        this.name = name;
    }




    public String getName() {
        return name;
    }

    /**
     *
     * @return if market is currently open. When implementing account for some safety margin - would be bad to execute an order at the instant the market closes.
     * TODO make abstract.
     * TODO better to have MarketEnum which knows its opening times.
     */
    public Boolean isMarketOpen() {
        return true;
    }


}

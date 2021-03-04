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


}

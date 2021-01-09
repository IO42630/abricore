package com.olexyn.abricore.model.options;

import com.olexyn.abricore.model.Asset;

public abstract class Option extends Asset {

    private Asset underlyingAsset;


    public Option(String name) {
        super(name);
    }
}

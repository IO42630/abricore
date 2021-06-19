package com.olexyn.abricore.model;

import com.olexyn.abricore.model.options.OtcOption;

import java.util.HashMap;
import java.util.Map;

public class Commodity extends Asset implements UnderlyingAsset{

    Map<String, Etf> etfs = new HashMap<>();
    Map<String, OtcOption> options = new HashMap<>();

    public Commodity(String name) {
        super(name);
    }
}

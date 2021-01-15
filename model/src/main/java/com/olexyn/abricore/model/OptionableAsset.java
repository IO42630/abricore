package com.olexyn.abricore.model;

import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.options.VanillaOption;

import java.util.HashMap;
import java.util.Map;

public abstract class OptionableAsset extends Asset{

    public Map<String, Option> options = new HashMap<>();

    public Map<Double,Double> resistances = new HashMap<>();


    public OptionableAsset(String name) {
        super(name);
    }
}

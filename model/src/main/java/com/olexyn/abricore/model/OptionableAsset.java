package com.olexyn.abricore.model;

import com.olexyn.abricore.model.options.VanillaOption;

import java.util.HashMap;
import java.util.Map;

public abstract class OptionableAsset extends Asset{

    public Map<String, VanillaOption> options = new HashMap<>();

}

package com.olexyn.abricore.model;

import com.olexyn.abricore.model.options.OtcOption;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Commodity extends Asset{

    Map<String, Etf> etfs = new HashMap<>();
    Map<String, OtcOption> options = new HashMap<>();
}

package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Commodity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Central instance for defining Tickers.
 */
public class Symbols {

    final static Set<Asset> SYMBOLS = new HashSet<>();

    static {
        SYMBOLS.add(new Commodity("XAGUSD"));
    }

    /**
     */
    public static Set<Asset> getList() {
        return SYMBOLS;
    }

    public static Asset getAsset(String symbol) {
        return SYMBOLS.stream().filter(x -> x.getName().equals(symbol)).findAny().orElseThrow();
    }

    public static Set<String> getNames() {
        return SYMBOLS.stream().map(Asset::getName).collect(Collectors.toSet());
    }
}

package com.olexyn.abricore.datastore.symbols;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Commodity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Symbols {

    final static Set<Asset> SYMBOLS = new HashSet<>();

    static {
        SYMBOLS.add(new Commodity("XAGUSD"));
    }

    /**
     * Stub ...
     */
    public static Set<Asset> getList() {
        return SYMBOLS;
    }

    public static Set<String> getNames() {
        return SYMBOLS.stream().map(Asset::getName).collect(Collectors.toSet());
    }
}

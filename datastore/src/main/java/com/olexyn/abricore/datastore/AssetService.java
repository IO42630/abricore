package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Commodity;
import com.olexyn.abricore.model.Stock;
import com.olexyn.abricore.model.options.BarrierOption;

import static com.olexyn.abricore.datastore.SymbolsService.SYMBOLS;

public class AssetService {

    public static Asset ofName(String name) {
        return SYMBOLS.stream().filter(x -> x.getName().equals(name)).findAny().orElseThrow();
    }

    public static Asset ofIsin(String isin) {
        return SYMBOLS.stream().filter(x -> x.getSqIsin().equals(isin)).findAny().orElseThrow();
    }

    public static Asset ofTwSymbol(String twSymbol) {
        return SYMBOLS.stream().filter(x -> x.getTwSymbol().equals(twSymbol)).findAny().orElseThrow();
    }

    public static void addAsset(Asset asset) {
        SYMBOLS.add(asset);
    }

}

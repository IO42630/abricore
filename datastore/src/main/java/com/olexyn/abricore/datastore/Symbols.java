package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.AssetType;
import com.olexyn.abricore.model.Commodity;
import com.olexyn.abricore.model.GenericAsset;
import com.olexyn.abricore.model.Stock;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.util.FileUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Central instance for defining Tickers.
 */
public class Symbols {

    final static Set<Asset> SYMBOLS = new HashSet<>();

    static {
        loadSymbols();
    }

    /**
     */
    public static Set<Asset> getList() {
        return SYMBOLS;
    }

    public static Asset ofName(String name) {
        return SYMBOLS.stream().filter(x -> x.getName().equals(name)).findAny().orElseThrow();
    }

    public static Asset ofIsin(String isin) {
        return SYMBOLS.stream().filter(x -> x.getSqIsin().equals(isin)).findAny().orElseThrow();
    }

    public static Asset ofTwSymbol(String twSymbol) {
        return SYMBOLS.stream().filter(x -> x.getTwSymbol().equals(twSymbol)).findAny().orElseThrow();
    }

    public static Set<String> getNames() {
        return SYMBOLS.stream().map(Asset::getName).collect(Collectors.toSet());
    }

    private static void loadSymbols() {
        String path = Symbols.class.getClassLoader().getResource("symbols.json").getPath();

        String contents = new FileUtil().fileToString(new File(path));

        try {
            JSONArray symbols = new JSONObject(contents).getJSONArray("SYMBOLS");

            for (int i =0 ; i < symbols.length(); i++) {
                JSONObject symbol = symbols.getJSONObject(i);
                String name = symbol.getString("NAME");
                Asset asset;
                switch (AssetType.valueOf(symbol.getString("TYPE"))) {
                    case COMMODITY:
                        asset = new Commodity(name);
                        break;
                    case STOCK:
                        asset = new Stock(name);
                        break;
                    case BARRIER_OPTION:
                        asset = new BarrierOption(name);
                        break;
                    default:
                        asset = new GenericAsset("unknown");
                        break;
                }

                asset.setTwSymbol(symbol.getString("TW_SYMBOL"));
                asset.setSqIsin(symbol.getString("SQ_ISIN"));
                SYMBOLS.add(asset);
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}

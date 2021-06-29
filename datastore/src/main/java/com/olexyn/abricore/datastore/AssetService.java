package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.AssetType;
import com.olexyn.abricore.model.Commodity;
import com.olexyn.abricore.model.Crypto;
import com.olexyn.abricore.model.Etf;
import com.olexyn.abricore.model.Stock;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.FileUtil;
import com.olexyn.abricore.util.LogUtil;
import com.olexyn.abricore.util.Parameters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.DataUtil.JsonKeys;

public class AssetService {

    private static final Logger LOGGER = LogUtil.get(AssetService.class);

    private static final String SYM = JsonKeys.SYMBOLS.name();
    private static final String NAME = JsonKeys.NAME.name();
    private static final String TYPE = JsonKeys.TYPE.name();
    private static final String OPTION_TYPE = JsonKeys.OPTION_TYPE.name();
    private static final String UNDERLYING = JsonKeys.UNDERLYING.name();
    private static final String STRIKE = JsonKeys.STRIKE.name();
    private static final String TW_SYMBOL = JsonKeys.TW_SYMBOL.name();
    private static final String SQ_ISIN = JsonKeys.SQ_ISIN.name();

    public final static Set<Asset> SYMBOLS = new HashSet<>();

    static {

        String contents = new FileUtil().fileToString(new File(Parameters.SYMBOLS_PATH));

        try {
            JSONArray symbols = new JSONObject(contents).getJSONArray(SYM);

            for (int i =0 ; i < symbols.length(); i++) {
                JSONObject symbol = symbols.getJSONObject(i);
                String name = symbol.getString(NAME);
                Asset asset;
                AssetType assetType = AssetType.valueOf(symbol.getString(TYPE));
                switch (assetType) {
                    case COMMODITY:
                        asset = new Commodity(name);
                        break;
                    case STOCK:
                        asset = new Stock(name);
                        break;
                    case BARRIER_OPTION:
                        asset = new BarrierOption(name);
                        Option option = (Option) asset;
                        option.setOptionType(OptionType.valueOf(symbol.getString(OPTION_TYPE)));
                        option.setUnderlying(AssetService.ofName(symbol.getString(UNDERLYING)));
                        option.setStrike(ANum.of(symbol.getString(STRIKE)));
                        break;
                    case ETF:
                        asset = new Etf(name);
                        break;
                    case CRYPTO:
                        asset = new Crypto(name);
                        break;
                    default:
                        LOGGER.severe("ERROR: unknown asset type.");
                        throw new StoreException();
                }
                asset.setAssetType(assetType);
                asset.setTwSymbol(symbol.getString(TW_SYMBOL));
                asset.setSqIsin(symbol.getString(SQ_ISIN));
                SYMBOLS.add(asset);
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getNames() {
        return SYMBOLS.stream().map(Asset::getName).collect(Collectors.toSet());
    }

    public static void save() throws IOException {
        LOGGER.info("START saving SYMBOLS to JSON.");
        JSONObject symbolsJson = new JSONObject();
        JSONArray symbols = new JSONArray();
        for (Asset asset : SYMBOLS) {
            JSONObject assetJson = new JSONObject();
            assetJson.put(NAME, asset.getName());
            assetJson.put(TW_SYMBOL, asset.getTwSymbol());
            assetJson.put(TYPE, asset.getAssetType().name());
            assetJson.put(SQ_ISIN, asset.getSqIsin());
            if (asset instanceof Option) {
                Option option = (Option) asset;
                assetJson.put(STRIKE, option.getStrike());
                assetJson.put(OPTION_TYPE, option.getOptionType());
                assetJson.put(UNDERLYING, option.getUnderlying().getName());
            }
            symbols.put(assetJson);
        }
        symbolsJson.put(SYM, symbols);
        FileWriter fw = new FileWriter(Parameters.SYMBOLS_TEST_PATH);

        fw.write(DataUtil.prettyJson(symbolsJson));
        fw.flush();
        fw.close();
        LOGGER.info("FINISH saving SYMBOLS to JSON.");
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

    public static void addAsset(Asset asset) {
        SYMBOLS.add(asset);
    }

}

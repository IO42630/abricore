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
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.DataUtil.JsonKeys;

public class AssetService {

    private static final Logger LOGGER = LogUtil.get(AssetService.class);

    private static final String SYMBOLS = JsonKeys.SYMBOLS.name();
    private static final String OPTIONS = JsonKeys.OPTIONS.name();
    private static final String NAME = JsonKeys.NAME.name();
    private static final String TYPE = JsonKeys.TYPE.name();
    private static final String OPTION_TYPE = JsonKeys.OPTION_TYPE.name();
    private static final String UNDERLYING = JsonKeys.UNDERLYING.name();
    private static final String STRIKE = JsonKeys.STRIKE.name();
    private static final String TW_SYMBOL = JsonKeys.TW_SYMBOL.name();
    private static final String SQ_ISIN = JsonKeys.SQ_ISIN.name();
    private static final String CURRENCY = JsonKeys.CURRENCY.name();
    private static final String EXCHANGE = JsonKeys.EXCHANGE.name();

    public final static Set<Asset> ASSETS = new HashSet<>();

    public static void loadAssets() {
        LOGGER.info("START loading ASSETS from JSON.");
        try {
            loadSymbols();
            loadOptions();
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        LOGGER.info("FINISH loading ASSETS from JSON.");
    }


    private static void loadSymbols() {
        String contents = new FileUtil().fileToString(new File(Parameters.SYMBOLS_PATH));
        JSONArray symbols = new JSONObject(contents).getJSONArray(SYMBOLS);

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
            ASSETS.add(asset);
        }
    }

    private static void loadOptions() {
        String contents = new FileUtil().fileToString(new File(Parameters.OPTIONS_PATH));
        JSONArray options = new JSONObject(contents).getJSONArray(OPTIONS);

        for (int i =0 ; i < options.length(); i++) {
            JSONObject optionJson = options.getJSONObject(i);
            String name = optionJson.getString(NAME);
            Option option;
            AssetType assetType = AssetType.valueOf(optionJson.getString(TYPE));
            switch (assetType) {
                case BARRIER_OPTION:
                    option = new BarrierOption(name);
                    break;
                default:
                    LOGGER.severe("ERROR: unknown asset type.");
                    throw new StoreException();
            }
            option.setAssetType(assetType);
            option.setSqIsin(optionJson.getString(SQ_ISIN));
            option.setStrike(ANum.of(optionJson.getString(STRIKE)));
            option.setOptionType(OptionType.valueOf(optionJson.getString(OPTION_TYPE)));
            option.setUnderlying(AssetService.ofName(optionJson.getString(UNDERLYING)));
            option.setCurrency(Currency.valueOf(optionJson.getString(CURRENCY)));
            option.setExchange(Exchange.valueOf(optionJson.getString(EXCHANGE)));
            ASSETS.add(option);
        }
    }

    public static Set<String> getNames() {
        return ASSETS.stream().map(Asset::getName).collect(Collectors.toSet());
    }

    public static void save() throws IOException {
        LOGGER.info("START saving ASSETS to JSON.");
        saveSymbols();
        saveOptions();
        LOGGER.info("FINISH saving ASSETS to JSON.");
    }

    private static void saveSymbols() throws IOException {
        JSONObject symbolsJson = new JSONObject();
        JSONArray symbols = new JSONArray();
        List<Asset> symbolsList = ASSETS.stream()
            .filter(x -> !(x instanceof Option))
            .collect(Collectors.toList());
        for (Asset asset : symbolsList) {
            JSONObject assetJson = new JSONObject();
            assetJson.put(NAME, asset.getName());
            assetJson.put(TW_SYMBOL, asset.getTwSymbol());
            assetJson.put(TYPE, asset.getAssetType().name());
            assetJson.put(SQ_ISIN, asset.getSqIsin());
            symbols.put(assetJson);
        }
        symbolsJson.put(SYMBOLS, symbols);
        FileWriter fw = new FileWriter(Parameters.SYMBOLS_PATH);

        fw.write(DataUtil.prettyJson(symbolsJson));
        fw.flush();
        fw.close();
    }

    private static void saveOptions() throws IOException {
        JSONObject optionsJson = new JSONObject();
        JSONArray options = new JSONArray();
        List<Option> optionsList = ASSETS.stream()
            .filter(x -> x instanceof Option)
            .map(x -> (Option) x)
            .collect(Collectors.toList());
        for (Option option : optionsList) {
            JSONObject assetJson = new JSONObject();
            assetJson.put(NAME, option.getName());
            assetJson.put(TW_SYMBOL, option.getTwSymbol());
            assetJson.put(TYPE, option.getAssetType().name());
            assetJson.put(SQ_ISIN, option.getSqIsin());
            assetJson.put(STRIKE, option.getStrike());
            assetJson.put(OPTION_TYPE, option.getOptionType());
            assetJson.put(UNDERLYING, option.getUnderlying().getName());
            assetJson.put(CURRENCY, option.getCurrency());
            assetJson.put(EXCHANGE, option.getExchange().name());
            options.put(assetJson);
        }
        optionsJson.put(OPTIONS, options);

        FileWriter fw = new FileWriter(Parameters.OPTIONS_PATH);
        fw.write(DataUtil.prettyJson(optionsJson));
        fw.flush();
        fw.close();
    }

    public static Asset ofName(String name) {
        return ASSETS.stream().filter(x -> x.getName().equals(name)).findAny().orElseThrow();
    }

    public static Asset ofIsin(String isin) {
        return ASSETS.stream().filter(x -> x.getSqIsin().equals(isin)).findAny().orElseThrow();
    }

    public static Asset ofTwSymbol(String twSymbol) {
        return ASSETS.stream().filter(x -> x.getTwSymbol().equals(twSymbol)).findAny().orElseThrow();
    }

    public synchronized static void addAsset(Asset asset) {
        ASSETS.add(asset);
    }

}

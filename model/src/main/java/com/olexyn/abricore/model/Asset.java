package com.olexyn.abricore.model;



import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;

import java.util.Objects;


public abstract class Asset {

    private AssetType assetType;

    private final String name;

    private String twSymbol;

    private String sqIsin;

    private Currency currency;

    private Exchange exchange;

    public Asset(String name) {
        this.name = name;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getName() {
        return name;
    }

    public String getTwSymbol() {
        return twSymbol;
    }

    public void setTwSymbol(String twSymbol) {
        this.twSymbol = twSymbol;
    }

    public String getSqIsin() {
        return sqIsin;
    }

    public void setSqIsin(String sqIsin) {
        this.sqIsin = sqIsin;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    /**
     *
     * @return if market is currently open. When implementing account for some safety margin - would be bad to execute an order at the instant the market closes.
     * TODO make abstract.
     * TODO better to have MarketEnum which knows its opening times.
     */
    public Boolean isMarketOpen() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Asset asset = (Asset) o;
        return assetType == asset.assetType &&
            name.equals(asset.name) &&
            Objects.equals(twSymbol, asset.twSymbol) &&
            Objects.equals(sqIsin, asset.sqIsin) &&
            currency == asset.currency &&
            exchange == asset.exchange;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetType, name, twSymbol, sqIsin, currency, exchange);
    }
}

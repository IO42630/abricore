package com.olexyn.abricore.model.runtime.assets;



import com.olexyn.abricore.model.runtime.Dto;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Objects;
import java.util.Optional;

import static com.olexyn.abricore.model.runtime.assets.AssetType.UNKNOWN;
import static com.olexyn.abricore.util.Constants.L_BRACE;
import static com.olexyn.abricore.util.Constants.R_BRACE;
import static com.olexyn.abricore.util.Constants.SPACE;

@Getter
@Setter
public abstract class AssetDto implements Dto<AssetDto> {

    @Serial
    private static final long serialVersionUID = 9060942829581220194L;

    private Long id = null;

    private AssetType assetType = null;

    private String name = null;

    private String twSymbol = null;

    private String sqIsin = null;

    private Currency currency = null;

    private Exchange exchange = null;

    protected AssetDto() { }

    protected AssetDto(String name) { this.name = name; }

    public void setAssetType(AssetType assetType) {
        if (assetType == null) { return; }
        this.assetType = assetType;
    }

    public void setTwSymbol(String twSymbol) {
        if (twSymbol == null) { return; }
        this.twSymbol = twSymbol;
    }

    public void setSqIsin(String sqIsin) {
        if (sqIsin == null) { return; }
        this.sqIsin = sqIsin;
    }

    public void setCurrency(Currency currency) {
        if (currency == null) { return; }
        this.currency = currency;
    }

    public void setExchange(Exchange exchange) {
        if (exchange == null) { return; }
        this.exchange = exchange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AssetDto asset = (AssetDto) o;
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

    @Override
    public String toString() {
        var type = Optional.ofNullable(assetType).orElse(UNKNOWN);
        return name + SPACE + L_BRACE + type.name() + R_BRACE;
    }

    @Override
    public boolean isComplete() {
        return assetType != null
            && name != null
            && (twSymbol != null || sqIsin != null)
            && currency != null
            && exchange != null;
    }

    @Override
    public AssetDto mergeFrom(AssetDto other) {
        setAssetType(other.getAssetType());
        setTwSymbol(other.getTwSymbol());
        setSqIsin(other.getSqIsin());
        setCurrency(other.getCurrency());
        setExchange(other.getExchange());
        return this;
    }

}

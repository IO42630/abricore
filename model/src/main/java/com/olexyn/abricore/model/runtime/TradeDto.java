package com.olexyn.abricore.model.runtime;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.util.enums.TradeStatus;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import static com.olexyn.abricore.util.Constants.NULL_STR;
import static com.olexyn.abricore.util.Constants.SPACE;
import static com.olexyn.abricore.util.num.NumUtil.prettyStr;

@Getter
public class TradeDto implements Serializable, Dto<TradeDto>, AssetHolder {

    @Serial
    private static final long serialVersionUID = 3884219176680640506L;

    private Long id = null;
    private AssetDto asset = null;
    private TradeStatus status = null;
    private Instant buyInstant = null;
    private Instant sellInstant = null;
    private long amount;
    private long buyPrice;
    private long sellPrice;
    private String buyId = null;
    private String sellId = null;
    private long buyFee;
    private long sellFee;
    private UUID uuid;


    /**
     * Guaranteed to have an UUID. This does not mean that instances can't 'shadow' each other. Use buyId/sellId to merge 'shadows'.
     */
    public TradeDto(AssetDto asset) {
        uuid = UUID.randomUUID();
        setAsset(asset);
    }

    @Override
    public boolean isComplete() {
        return getAsset().isComplete();
    }

    public void setId(Long id) {
        if (this.id != null) { return; }
        this.id = id;
    }

    @Override
    public void setAsset(@Nullable AssetDto asset) {
        if (asset == null) { return; }
        this.asset = asset;
    }

    public void setStatus(@Nullable TradeStatus status) {
        if (status == null) { return; }
        this.status = status;
    }

    public void setBuyInstant(@Nullable Instant buyInstant) {
        if (buyInstant == null) { return; }
        this.buyInstant = buyInstant;
    }

    public void setSellInstant(@Nullable Instant sellInstant) {
        if (sellInstant == null) { return; }
        this.sellInstant = sellInstant;
    }

    public void setAmount(long amount) {
        if (0 == (amount)) { return; }
        this.amount = amount;
    }

    public void setBuyPrice(long buyPrice) {
        if (0 == (buyPrice)) { return; }
        this.buyPrice = buyPrice;
    }

    public void setSellPrice(long sellPrice) {
        if (0 == (sellPrice)) { return; }
        this.sellPrice = sellPrice;
    }

    public void setBuyId(String buyId) {
        if (this.buyId != null) { return; }
        this.buyId = buyId;
    }

    public void setSellId(String sellId) {
        if (this.sellId != null) { return; }
        this.sellId = sellId;
    }

    public void setBuyFee(long buyFee) {
        if (0 == (buyFee)) { return; }
        this.buyFee = buyFee;
    }

    public void setSellFee(long sellFee) {
        if (0 == (sellFee)) { return; }
        this.sellFee = sellFee;
    }

    public void setUuid(@Nullable UUID uuid) {
        if (uuid == null) { return; }
        this.uuid = uuid;
    }

    @Override
    public TradeDto mergeFrom(TradeDto other) {
        this.id = other.getId();
        this.asset = other.getAsset();
        this.status = other.getStatus();
        this.buyInstant = other.getBuyInstant();
        this.sellInstant = other.getSellInstant();
        setAmount(other.getAmount());
        setBuyPrice(other.getBuyPrice());
        setSellPrice(other.getSellPrice());
        this.buyId = other.getBuyId();
        this.sellId = other.getSellId();
        setBuyFee(other.getBuyFee());
        setSellFee(other.getSellFee());
        setUuid(other.getUuid());
        return this;
    }

    @Override
    public String toString() {
        return String.join(
            SPACE, asset == null ? NULL_STR : asset.toString(),
            "  ", amount == 0 ? NULL_STR : prettyStr(amount, 0),
            "  B", buyPrice == 0 ? NULL_STR : prettyStr(buyPrice, 2),
            " S", sellPrice == 0 ? NULL_STR : prettyStr(sellPrice, 2),
            "  ", buyInstant == null ? NULL_STR : buyInstant.toString(),
            " ", sellInstant == null ? NULL_STR : sellInstant.toString()
        );
    }

}



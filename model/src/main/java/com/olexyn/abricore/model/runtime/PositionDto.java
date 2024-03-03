package com.olexyn.abricore.model.runtime;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.util.enums.PositionStatus;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.io.Serializable;



@Getter
public class PositionDto implements Serializable, Dto<PositionDto>, AssetHolder {

    @Serial
    private static final long serialVersionUID = -588196728393639962L;
    private Long id = null;
    private AssetDto asset = null;
    private PositionStatus status = null;
    private long amount;
    private long price;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) { return; }
        this.id = id;
    }

    @Override
    public AssetDto getAsset() {
        return asset;
    }

    @Override
    public void setAsset(@Nullable AssetDto asset) {
        if (asset == null) { return; }
        this.asset = asset;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public void setStatus(@Nullable PositionStatus status) {
        if (status == null) { return; }
        this.status = status;
    }

    public void setAmount(long amount) {
        if (amount == 0) { return; }
        this.amount = amount;
    }

    public void setPrice(long price) {
        if (price == 0) { return; }
        this.price = price;
    }

    @Override
    public boolean isComplete() {
        return id != null
            && asset != null
            && status != null
            && amount != 0
            && price != 0;
    }

    @Override
    public PositionDto mergeFrom(PositionDto other) {
        setId(other.getId());
        setAsset(other.getAsset());
        setStatus(other.getStatus());
        setAmount(other.getAmount());
        setPrice(other.getPrice());
        return this;
    }
}



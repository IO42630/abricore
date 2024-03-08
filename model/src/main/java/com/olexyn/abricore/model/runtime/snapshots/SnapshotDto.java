package com.olexyn.abricore.model.runtime.snapshots;

import com.olexyn.abricore.model.runtime.AssetHolder;
import com.olexyn.abricore.model.runtime.Dto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.util.exception.DataCorruptionException;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.time.Instant;
import java.util.Objects;

import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumCalc.div;

@Getter
@Setter
public class SnapshotDto implements Dto<SnapshotDto>, AssetHolder {

    @Serial
    private static final long serialVersionUID = -7569214188842994352L;

    private Long id = null;

    /**
     * Touched is set to true when the SnapShotDto is created or modified. <br>
     * If the intention is for the SnapShotDto to be considered non-touched, <br>
     * e.g. when mapping from SnapShotEntity, <br>
     * then set touched to false manually.
     */
    private boolean touched;
    private AssetDto asset = null;

    private Instant instant = null;
    private long tradePrice;
    private long bidPrice;
    private long askPrice;

    private long volume;
    private long range;

    public SnapshotDto() {
        this.touched = true;
    }

    public SnapshotDto(@NonNull AssetDto asset) {
        setAsset(asset);
        this.touched = true;
    }


    @Override
    public void setAsset(@Nullable AssetDto asset) {
        if (asset == null) { return; }
        if (this.asset == null) {
            this.asset = asset;
            setTouched(true);
        }
    }



    public void setInstant(@Nullable Instant instant) {
        if (instant == null) { return; }
        if (this.instant == null) {
            this.instant = instant;
            setTouched(true);
        }
    }

    public void setTradePrice(long tradePrice) {
        if (this.tradePrice != 0 || tradePrice == 0) { return; }
        this.tradePrice = tradePrice;
        setTouched(true);
    }

    /**
     * Geldkurs
     */
    public void setBidPrice(long bidPrice) {
        if (this.bidPrice != 0 || bidPrice == 0) { return; }
        this.bidPrice = bidPrice;
        setTouched(true);
    }

    /**
     * Briefkurs
     */
    public void setAskPrice(long askPrice) {
        if (this.askPrice != 0) { return; }
        if (askPrice == 0) { return; }
        this.askPrice = askPrice;
        setTouched(true);
    }

    /**
     * Some Price Estimate / Best Known
     */
    public long getSomePrice() {
        var vTrade = getTradePrice() == 0 ? 0 : getTradePrice();
        var vAsk = getAskPrice() == 0 ? 0 : getAskPrice();
        var vBid = getBidPrice() == 0 ? 0 : getBidPrice();
        if (vTrade != 0) { return vTrade; }
        return div(vAsk + vBid, TWO);

    }

    public void setVolume(long volume) {
        if (this.volume != 0 || volume == 0) { return; }
        this.volume = volume;
        setTouched(true);
    }

    public void setRange(long range) {
        if (this.range != 0 || range == 0) { return; }
        this.range = range;
        setTouched(true);
    }

    public long getSpread() {
        return askPrice - bidPrice;
    }

    @Override
    public boolean isComplete() {
        return asset != null
            && instant != null
            && (tradePrice != 0 || bidPrice != 0);
    }

    /**
     * Merge for indicators is not needed.
     */
    @Override
    public SnapshotDto mergeFrom(SnapshotDto other) {
        boolean sameAsset = this.getAsset().equals(other.getAsset());
        boolean sameInstant = this.getInstant().equals(other.getInstant());
        if (!sameAsset || !sameInstant) {
            throw new DataCorruptionException();
        }
        setTradePrice(other.getTradePrice());
        setBidPrice(other.getBidPrice());
        setAskPrice(other.getAskPrice());
        setVolume(other.getVolume());
        setRange(other.getRange());
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        SnapshotDto that = (SnapshotDto) o;
        return asset.equals(that.asset) && instant.equals(that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset, instant);
    }

}

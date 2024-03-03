package com.olexyn.abricore.model.runtime.assets;

import com.olexyn.abricore.util.enums.OptionStatus;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;

import static com.olexyn.abricore.model.runtime.assets.OptionType.CALL;
import static com.olexyn.abricore.util.Constants.SPACE;
import static com.olexyn.abricore.util.num.NumUtil.prettyStr;

@Getter
@Setter
public class OptionDto extends AssetDto implements Comparable<OptionDto> {

    @Serial
    private static final long serialVersionUID = 329777350971710015L;

    private AssetDto underlying;
    private long strike;
    private Instant expiry = null;
    private long ratio;
    private OptionType optionType = null;
    private OptionStatus status = null;

    public OptionDto() { super(); }

    public OptionDto(String name) { super(name); }

    public void setStrike(long strike) {
        if (strike == 0) { return; }
        this.strike = strike;
    }

    public void setUnderlying(@Nullable AssetDto underlying) {
        if (underlying == null) { return; }
        this.underlying = underlying;
    }

    public Duration getTimeTillExpiry() {
        return Duration.between(Instant.now(), expiry);
    }

    public void setExpiry(@Nullable Instant expiry) {
        if (expiry == null) { return; }
        this.expiry = expiry;
    }

    public void setRatio(long ratio) {
        if (ratio == 0) { return; }
        this.ratio = ratio;
    }

    public void setOptionType(@Nullable OptionType optionType) {
        if (optionType == null) { return; }
        this.optionType = optionType;
    }

    public void setStatus(@Nullable OptionStatus status) {
        if (status == null) { return; }
        this.status = status;
    }



    @Override
    public String toString() {
        String typeString = optionType == CALL ? "C" : "P";
        return String.join(
            SPACE,
            underlying.getName(),
            typeString,
            prettyStr(strike, 2)
        );
    }

    @Override
    public boolean isComplete() {
        return super.isComplete()
            && underlying != null
            && strike != 0
            && expiry != null
            && ratio != 0
            && optionType != null;
    }

    @Override
    public OptionDto mergeFrom(AssetDto other) {
        super.mergeFrom(other);
        if (other instanceof OptionDto otherDto) {
            setUnderlying(otherDto.getUnderlying());
            setStrike(otherDto.getStrike());
            setExpiry(otherDto.getExpiry());
            setRatio(otherDto.getRatio());
            setOptionType(otherDto.getOptionType());
            setStatus(otherDto.getStatus());
        }
        return this;
    }

    /**
     * Sort options by strike.
     * Call options are sorted largest strike to smallest strike.
     * Put options are sorted smallest strike to largest strike.
     */
    @Override
    public int compareTo(OptionDto o) {
        if (this.getOptionType() != o.getOptionType()) {
            throw new IllegalArgumentException("Cannot compare options of different type.");
        }
        if (this.getOptionType() == CALL) {
            return this.getStrike() > o.getStrike() ? -1 : 1;
        }
        return o.getStrike() > this.getStrike() ? -1 : 1;
    }

}

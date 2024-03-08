package com.olexyn.abricore.model.runtime.snapshots;

import com.olexyn.abricore.model.runtime.Dto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.enums.FrameType;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Space between two adjacent Instants in a Series.
 */
@Getter
public class FrameDto implements Dto<FrameDto> {

    @Serial
    private static final long serialVersionUID = 8175538251022363241L;

    @Setter
    private Long id;

    @Setter
    private FrameType frameType;

    private final Instant start;

    @Setter
    @Nullable
    private Instant end = null;

    @Setter
    private Duration duration;

    private final Series series;

    public FrameDto(Series series, Instant start) {
        this.start = start;
        this.series = series;
        if (start != null) {
            setEnd(series.higherKey(start));
        }
    }

    public AssetDto getAsset() {
        return series.getAsset();
    }

    public LocalDate getStartDate() {
        return DataUtil.getLocalDate(start);
    }

    public @Nullable LocalDate getEndDate() {
        if (end == null) {
            return null;
        }
        return DataUtil.getLocalDate(end);
    }




    public Duration getDuration() {
        if (duration != null || getStart() == null || getEnd() == null) {
            return duration;
        }
        duration = Duration.between(getStart(), getEnd());
        return duration;
    }

    public final @Nullable FrameDto next() {
        if (getEnd() == null) {
            return null;
        }
        return new FrameDto(series, getEnd());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public FrameDto mergeFrom(FrameDto other) {
        return new FrameDto(other.getSeries(), other.getStart());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frameType, start, end, series);
    }

}

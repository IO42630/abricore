package com.olexyn.abricore.model.runtime.snapshots;

import com.olexyn.abricore.model.runtime.assets.AssetDto;

import java.time.Duration;
import java.time.Instant;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;

public interface ISeries {

    AssetDto getAsset();

    Instant getFirstKey();

    Instant getLastKey();

    SnapshotDto getLast();

    Instant getSameOrFirstBefore(Instant instant);

    Instant getSameOrFirstAfter(Instant instant);

    Instant higherKey(Instant key);

    Instant lowerKey(Instant key);

    NavigableSet<Instant> getNavSet();

    int size();

    void clear();

    SnapshotDto getSnapshot(Instant instant);

    long getLastTraded();

    SnapshotDto getSnapshotBeforeOffset(Duration offset);

    NavigableMap<Instant, SnapshotDto> getSection(Duration offset);

    NavigableMap<Instant, SnapshotDto> getSection(Duration offset, Duration duration);

    long growth(Duration offset, Duration duration);

    long ma(Duration offset, Duration duration);

    long std(Duration offset, Duration duration, Optional<Long> maO);

    long bolRadius(Duration offset, Duration duration, long bolTimes, Optional<Long> maO);

    SnapshotDistanceDto getSnapshotDistance(Instant start);

    void putNoWait(SnapshotDto snapshot);

    void put(SnapshotDto snapshot);

    boolean priceInBol(Duration offset, Duration duration, long bolTimes);

    long[] avgUpDown(Duration duration);

    long rs(Duration duration);

    long rsi(Duration duration);

    void patch();

}

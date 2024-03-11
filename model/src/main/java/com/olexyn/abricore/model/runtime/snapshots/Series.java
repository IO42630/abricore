package com.olexyn.abricore.model.runtime.snapshots;

import com.olexyn.abricore.model.runtime.AObserver;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.util.enums.TimeSide;
import com.olexyn.abricore.util.exception.MissingException;
import com.olexyn.abricore.util.exception.SoftCalcException;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.propconf.PropConf;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;

import static com.olexyn.abricore.util.Constants.L_BRACE;
import static com.olexyn.abricore.util.Constants.R_BRACE;
import static com.olexyn.abricore.util.Constants.SPACE;
import static com.olexyn.abricore.util.enums.TimeSide.AFTER;
import static com.olexyn.abricore.util.enums.TimeSide.BEFORE;
import static com.olexyn.abricore.util.num.Num.EP2;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.NumCalc.abs;
import static com.olexyn.abricore.util.num.NumCalc.div;
import static com.olexyn.abricore.util.num.NumCalc.sqrt;
import static com.olexyn.abricore.util.num.NumCalc.square;
import static com.olexyn.abricore.util.num.NumCalc.times;
import static com.olexyn.abricore.util.num.NumUtil.fromInt;

/**
 * Series is a wrapper for a HashMap && TreeMap of Instant, SnapshotDto. <br>
 * Use HashMap for fast access. <br>
 * Use TreeMap for fast iteration. <br>
 * Each Asset has it's own Series. <br>
 * Series can be observed. <br>
 */
public class Series extends ProtoSeries implements Observable {

    private static final int PATCH_SIZE = PropConf.getInt("series.patch.size");
    private static final Duration WAIT_FOR_OBS_TO_RACT = PropConf.getDuration("trade.wait.for.observers.to.react.milli");

    private final List<AObserver> observers = new ArrayList<>();

    private final int sampleSize;



    private final AssetDto asset;

    public Series(AssetDto asset, int samleSize) {
        this.asset = asset;
        this.sampleSize = samleSize;
    }

    @Override
    public AssetDto getAsset() {
        return asset;
    }

    @Override
    public SnapshotDto getSnapshot(Instant instant) {
        if (instant == null) { throw new MissingException(); }
        var resultSnap = hashMap().get(instant);
        if (resultSnap == null) { throw new MissingException(); }
        return resultSnap;
    }

    @Override
    public long getLastTraded() {
        var lastSnap = getLast();
        if (lastSnap == null) { return 0; }
        return getLast().getTradePrice();
    }


    /**
     * Get the first Snapshot BEFORE the Instant determined by the given offset.
     */
    @Override
    public SnapshotDto getSnapshotBeforeOffset(Duration offset) {
        return hashMap().get(getKeyAtDurationOffset(offset, BEFORE));
    }

    @Override
    public NavigableMap<Instant, SnapshotDto> getSection(Duration offset) {
        Instant from = getKeyAtDurationOffset(offset, AFTER);
        return tree().tailMap(from, true);
    }

    /**
     * Get Section of the Series.
     * Includes the Snapshot at the offset.
     */
    @Override
    public NavigableMap<Instant, SnapshotDto> getSection(
        Duration offset,
        Duration duration
    ) {
        Instant from = getKeyAtDurationOffset(offset.plus(duration), AFTER);
        Instant to = getKeyAtDurationOffset(offset, BEFORE);
        if (from.isAfter(to)) {
            throw new SoftCalcException("Section likely empty. Refusing to continue due to risk of side effects."
            );
        }
        return tree().subMap(from, true, to, true);
    }

    @Override
    public long growth(Duration offset, Duration duration) {
        var from = getKeyAtDurationOffset(offset.plus(duration), AFTER);
        var to = getKeyAtDurationOffset(offset, BEFORE);
        long firstTradePrice = hashMap().get(from).getTradePrice();
        if (firstTradePrice == 0) { throw new MissingException(); }
        long lastTradePrice = hashMap().get(to).getTradePrice();
        if (lastTradePrice == 0) { throw new MissingException(); }
        return div(lastTradePrice, firstTradePrice);
    }

    /**
     * Moving Average.
     * The duration may be rather large, so we pick 50 samples from the section.
     * Example:
     * duration     section.size()   overRatio  safeOverRatio     result
     * 5m           150              3          3                 50 (150/3)
     * 1m           30               1          1                 30 (30/1)
     */
    @Override
    public long ma(Duration offset, Duration duration) {
        var tradeds = getSection(offset, duration).values().toArray(new SnapshotDto[0]);
        int overRatio = tradeds.length / sampleSize;
        int safeOverRatio = overRatio == 0 ? 1 : overRatio;
        long sum = 0;
        int sumSize = 0;
        for (int i = 0; i < tradeds.length; i++) {
            if (i % safeOverRatio == 0) {
                sum = sum + tradeds[i].getTradePrice();
                sumSize++;
            }
        }
        return div(sum, fromInt(sumSize));
    }

    /**
     * Standard Deviation.
     *
     * section size = 20
     * sample size = 10
     * sumSize      overRatio   modulo              percentage
     * 1            10/10 = 1   second * 10 % 1    100%
     * 2            20/10 = 2   second * 10 % 2    95%
     * 3            30/10 = 3   second * 10 % 3    33%
     *
     */
    @Override
    public long std(Duration offset, Duration duration, Optional<Long> maO) {
        long ma = maO.orElseGet(() -> ma(offset, duration));
        var tradeds = getSection(offset, duration).values().toArray(new SnapshotDto[0]);
        int overRatio = tradeds.length / sampleSize;
        int safeOverRatio = overRatio == 0 ? 1 : overRatio;
        long sum = 0;
        int sumSize = 0;
        for (int i = 0; i < tradeds.length; i++) {
            if (i % safeOverRatio == 0) {
                sum = sum + square(tradeds[i].getTradePrice() - ma);
                sumSize++;
            }
        }
        return sqrt(div(sum, fromInt(sumSize)));
    }


    /**
     * @param bolTimes : NUM
     */
    @Override
    public long bolRadius(Duration offset, Duration duration, long bolTimes, Optional<Long> maO) {
        return times(std(offset, duration, maO), bolTimes);
    }

    /**
     * Get Instant of Snapshot which is offset bars away from the last Snapshot.
     */
    private Instant getKeyAtDurationOffset(Duration duration, TimeSide side) {
        var result = getLastKey();
        if (result == null) { throw new MissingException("last key == null"); }
        if (duration.isZero()) { return result; }
        if (side == BEFORE) {
            result = getSameOrFirstBefore(result.minus(duration));
        } else {
            result = getSameOrFirstAfter(result.minus(duration));
        }
        if (result == null) { throw new MissingException("key at offset == null"); }
        return result;
    }

    @Override
    public SnapshotDistanceDto getSnapshotDistance(Instant start) {
        return new SnapshotDistanceDto(this, start);
    }

    public SnapshotDto lower(@Nullable SnapshotDto currentSnap) {
        if (currentSnap == null) { throw new MissingException(); }
        var lowerSnap = getSnapshot(tree().lowerKey(currentSnap.getInstant()));
        if (lowerSnap == null) { throw new MissingException(); }
        return lowerSnap;
    }

    /**
     * PUT SYNC, but do not WAIT for OBS to call back.
     */
    @Override
    public void putNoWait(SnapshotDto snapshot) {
        put(snapshot, false);
    }

    @Override
    public void put(SnapshotDto snapshot) {
        put(snapshot, true);
    }

    private void put(SnapshotDto snapshot, boolean wait) {
        if (snapshot.getInstant() == null) { return; }
        SnapshotDto existing = hashMap().get(snapshot.getInstant());
        if (existing != null) {
            snapshot = existing.mergeFrom(snapshot);
        }
        synchronized(this) {
            hashMap().put(snapshot.getInstant(), snapshot);
            tree().put(snapshot.getInstant(), snapshot);
            setLastPutKey(snapshot.getInstant());
        }
        if (wait) {
            notifyObservers();
        }
    }

    @Override
    public void notifyObservers() {
        for (AObserver observer : List.copyOf(observers)) {
            synchronized(observer.getLock()) {
                observer.getLock().safeWait(WAIT_FOR_OBS_TO_RACT);
            }
        }
    }


    /**
     * @param bolTimes : NUM
     */
    @Override
    public boolean priceInBol(Duration offset, Duration duration, long bolTimes) {
        long traded = getSnapshotBeforeOffset(offset).getTradePrice();
        assert Objects.nonNull(traded);
        long ma = ma(offset, duration);
        long lastPriceRadius = abs(traded - ma);
        long lastBolRadius = bolRadius(offset, duration, bolTimes, Optional.of(ma));
        return lastPriceRadius < lastBolRadius;
    }

    private long upMove(SnapshotDto from, SnapshotDto to) {
        assert Objects.nonNull(to.getTradePrice());
        long diff = to.getTradePrice() - from.getTradePrice();
        return diff > 0 ? diff : 0;
    }

    private long downMove(SnapshotDto from, SnapshotDto to) {
        assert Objects.nonNull(from.getTradePrice());
        long diff = from.getTradePrice() - to.getTradePrice();
        return diff > 0 ? diff : 0;
    }





    /**
     */
    @Override
    public long[] avgUpDown(Duration duration) {
        var section = getSection(duration);
        int size = section.size();
        if (size < 2) { return new long[]{0, 0}; }
        var current = section.lastEntry();
        long sumUpMoves = 0;
        long sumDownMoves = 0;
        Entry<Instant, SnapshotDto> previous = null;
        do {
            previous = section.lowerEntry(current.getKey());
            sumUpMoves = sumUpMoves + upMove(previous.getValue(), current.getValue());
            sumDownMoves = sumDownMoves + downMove(previous.getValue(), current.getValue());
            current = previous;
        } while (!previous.equals(section.firstEntry()));
        return new long[]{
            div(sumUpMoves, fromInt(size)),
            div(sumDownMoves, fromInt(size))
        };
    }



    @Override
    public long rs(Duration duration) {
        var avgUpDown = avgUpDown(duration);
        return div(
            avgUpDown[0],
            avgUpDown[1]
        );
    }


    @Override
    public long rsi(Duration duration) {
        return EP2 - div(
            EP2,
            ONE + rs(duration)
        );
    }

    /**
     * The historical 1S data has gaps. That is (probably) because if not trades happen, the bar is not recorded.
     * To patch those gaps we carry forward the last known price.
     * But we set volume and range to zero to avoid corrupting the volume and volatility data.
     * If the gap is > 20S we do not patch it.
     * This is because a large gap is not the result of omitted trade data, but an overall gap in historical data.
     */
    @Override
    public void patch() {
        LogU.infoStart("patch %s", getAsset());
        List<SnapshotDto> patches = new ArrayList<>();
        for (Instant instant : getNavSet()) {
            SnapshotDto currentSnapshot = getSnapshot(instant);
            if (currentSnapshot == null) { break; }
            Instant nextInstant = higherKey(instant);
            if (nextInstant != null) {
                Instant insertInstant = instant.plus(Duration.ofSeconds(1));
                List<SnapshotDto> localPatches = new ArrayList<>();
                while (insertInstant.isBefore(nextInstant)) {
                    SnapshotDto snapshotDto = new SnapshotDto(currentSnapshot.getAsset());
                    snapshotDto.setInstant(insertInstant);
                    snapshotDto.setTradePrice(currentSnapshot.getTradePrice());
                    snapshotDto.setAskPrice(currentSnapshot.getAskPrice());
                    snapshotDto.setBidPrice(currentSnapshot.getBidPrice());
                    snapshotDto.setVolume(0);
                    snapshotDto.setRange(0);
                    localPatches.add(snapshotDto);
                    if (localPatches.size() > PATCH_SIZE) {
                        localPatches.clear();
                        break;
                    }
                    insertInstant = insertInstant.plus(Duration.ofSeconds(1));
                }
                patches.addAll(localPatches);
            }
        }
        for (var patch : patches) {
            put(patch);
        }
        LogU.infoEnd("patch %s with %s", getAsset(), patches.size());
    }

    @Override
    public String toString() {
        return StringUtils.join(
            getClass().getSimpleName(), L_BRACE,
            hashCode(), R_BRACE, SPACE,
            asset.getName(), L_BRACE,
            size(), R_BRACE
        );
    }

    @Override
    public void addObverser(AObserver obs) {
        observers.add(obs);
    }

    @Override
    public void removeObserver(AObserver obs) {
        observers.remove(obs);
    }

}

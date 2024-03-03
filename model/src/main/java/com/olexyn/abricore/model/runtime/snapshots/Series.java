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

    private final List<AObserver> observers = new ArrayList<>();



    private final AssetDto asset;

    public Series(AssetDto asset) {
        this.asset = asset;
    }

    @Override
    public AssetDto getAsset() {
        return asset;
    }

    public SnapshotDto getSnapshot(Instant instant) {
        if (instant == null) { throw new MissingException(); }
        var resultSnap = hashMap().get(instant);
        if (resultSnap == null) { throw new MissingException(); }
        return resultSnap;
    }

    public long getLastTraded() {
        var lastSnap = getLast();
        if (lastSnap == null) { return 0; }
        return getLast().getTradePrice();
    }


    /**
     * Get the first Snapshot BEFORE the Instant determined by the given offset.
     */
    public SnapshotDto getSnapshotBeforeOffset(Duration offset) {
        return hashMap().get(getKeyAtDurationOffset(offset, BEFORE));
    }

    public NavigableMap<Instant, SnapshotDto> getSection(Duration offset) {
        Instant from = getKeyAtDurationOffset(offset, AFTER);
        return tree().tailMap(from, true);
    }

    /**
     * Get Section of the Series.
     * Includes the Snapshot at the offset.
     */
    public NavigableMap<Instant, SnapshotDto> getSection(
        Duration offset,
        Duration duration
    ) {
        Instant from = getKeyAtDurationOffset(offset.plus(duration), AFTER);
        Instant to = getKeyAtDurationOffset(offset, BEFORE);
        if (from.isAfter(to)) {
            LogU.finePlain("from %s > to %s", from, to);
            throw new SoftCalcException("" +
                "Section likely empty. Refusing to continue due to risk of side effects."
            );
        }
        return tree().subMap(from, true, to, true);
    }

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
     */
    public long ma(Duration offset, Duration duration) {
        var section = getSection(offset, duration);
        int samleSize = 50;
        int overRatio = section.size() / samleSize;
        int safeOverRatio = overRatio == 0 ? 1 : overRatio;
        long sum = section.values().stream()
            .filter(snap -> snap.getInstant().getEpochSecond() % safeOverRatio == 0)
            .limit(samleSize)
            .map(SnapshotDto::getTradePrice)
            .reduce(Long::sum).orElse(0L);
        return div(sum, fromInt(samleSize));
    }

    /**
     * Standard Deviation.
     */
    public long std(Duration offset, Duration duration) {
        long ma = ma(offset, duration);
        var section = getSection(offset, duration);
        long sumOfSquares = section.values().stream()
            .map(SnapshotDto::getTradePrice)
            .map(traded -> square(traded - (ma)))
            .reduce(Long::sum).orElse(0L);
        return sqrt(div(sumOfSquares, fromInt(section.size())));
    }


    /**
     * @param bolTimes : NUM
     */
    public long bolRadius(Duration offset, Duration duration, long bolTimes) {
        return times(std(offset, duration), bolTimes);
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

    public FrameDto getFrame(Instant start) {
        return new FrameDto(this, start);
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
    public void putNoWait(SnapshotDto snapshot) {
        put(snapshot, false);
    }

    public void put(SnapshotDto snapshot) {
        put(snapshot, true);
    }

    public void put(SnapshotDto snapshot, boolean wait) {
        snapshot.setSeries(this);
        if (snapshot.getInstant() == null) { return; }
        SnapshotDto existing = hashMap().get(snapshot.getInstant());
        if (existing != null) {
            snapshot = existing.mergeFrom(snapshot);
        }
        synchronized(this) {
            hashMap().put(snapshot.getInstant(), snapshot);
            tree().put(snapshot.getInstant(), snapshot);
        }
        if (wait) {
            notifyObservers();
        }
    }

    @Override
    public void notifyObservers() {
        for (AObserver observer : List.copyOf(observers)) {
            synchronized(observer.getLock()) {
                observer.getLock().safeWait(
                    PropConf.getDuration("trade.wait.for.observers.to.react.milli")
                );
            }
        }
    }


    /**
     * @param bolTimes : NUM
     */
    public boolean priceInBol(Duration offset, Duration duration, long bolTimes) {
        long traded = getSnapshotBeforeOffset(offset).getTradePrice();
        assert Objects.nonNull(traded);
        long ma = ma(offset, duration);
        long lastPriceRadius = abs(traded - ma);
        long lastBolRadius = bolRadius(offset, duration, bolTimes);
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
     * @param barAmount : NUM
     */
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



    public long rs(Duration duration) {
        var avgUpDown = avgUpDown(duration);
        return div(
            avgUpDown[0],
            avgUpDown[1]
        );
    }


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

    public void addObverser(AObserver obs) {
        observers.add(obs);
    }

    public void removeObserver(AObserver obs) {
        observers.remove(obs);
    }


}

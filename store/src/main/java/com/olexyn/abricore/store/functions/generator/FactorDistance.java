package com.olexyn.abricore.store.functions.generator;

import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.DistanceGenerator;
import com.olexyn.abricore.util.exception.MissingException;

import java.io.Serial;

import static com.olexyn.abricore.util.num.NumCalc.times;


/**
 *
 */
public class FactorDistance implements DistanceGenerator {

    @Serial
    private static final long serialVersionUID = 6137451025681545497L;

    private final long factor;

    public FactorDistance(long value) {
        this.factor = value;
    }

    @Override
    public long generate(Series series) {
        var snap = series.getLast();
        if (snap == null || factor == 0) { throw new MissingException(); }
        var tradePrice = series.getLast().getTradePrice();
        if (tradePrice == 0) { throw new MissingException(); }
        return times(tradePrice, factor);
    }
}

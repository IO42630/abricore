package com.olexyn.abricore.model.runtime.strategy.functions;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;

/**
 * A functional interface for testing a condition.
 * YES -> EXECUTE TRADE
 * NO  -> DO NOT EXECUTE TRADE
 * <p>
 * Commonly, if !ENABLED -> IGNORE_TRUE , thus do NOT combine with OR.
 */
@FunctionalInterface
public interface TransactionCondition {

    boolean test(Series series, TradeDto trade, VectorDto vector);

}

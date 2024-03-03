package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;

import java.io.Serial;

public class IgnoreFalse implements TransactionCondition {

    @Serial
    private static final long serialVersionUID = 6137452025681545497L;

    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        return false;
    }

}

package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class And implements TransactionCondition {

    @Serial
    private static final long serialVersionUID = 6137451025681545497L;

    private final List<TransactionCondition> tcs = new ArrayList<>();

    public And(TransactionCondition... conditions) {
        tcs.addAll(List.of(conditions));
    }

    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        for (var tc : tcs) {
            if (!tc.test(series, trade, vector)) {
                return false;
            }
        }
        return !tcs.isEmpty();
    }

}

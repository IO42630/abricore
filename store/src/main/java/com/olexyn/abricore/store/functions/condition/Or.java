package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Scope("prototype")
@Component
public class Or implements TransactionCondition {

    @Serial
    private static final long serialVersionUID = 6137451025681545497L;

    private final List<TransactionCondition> tcs = new ArrayList<>();

    @SuppressWarnings("ALL")
    public TransactionCondition init(TransactionCondition... conditions) {
        for (var tc : conditions) { tcs.add(tc); }
        return this;
    }

    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        if (tcs.isEmpty()) { return false; }
        return tcs.parallelStream().anyMatch(tc -> tc.test(series, trade, vector));
    }

}

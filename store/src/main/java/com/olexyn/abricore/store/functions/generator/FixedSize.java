package com.olexyn.abricore.store.functions.generator;

import com.olexyn.abricore.model.runtime.strategy.functions.SizingCondition;
import org.checkerframework.checker.nullness.qual.NonNull;


/**
 *
 */
public class FixedSize implements SizingCondition {


    private long fixedSize;

    public FixedSize(long value) {
        this.fixedSize = value;
    }

    @Override
    @NonNull
    public Long apply(@NonNull Long totalAmount) {
        return fixedSize;
    }

}

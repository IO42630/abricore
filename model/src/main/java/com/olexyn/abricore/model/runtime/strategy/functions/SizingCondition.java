package com.olexyn.abricore.model.runtime.strategy.functions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

@FunctionalInterface
public interface SizingCondition extends Function<Long, Long> {

    @Override
    @NonNull
    Long apply(@NonNull Long totalAmount);
}

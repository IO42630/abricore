package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.snapshots.Series;

import java.io.Serializable;
import java.util.function.Predicate;

@FunctionalInterface
public interface TransactionCondition extends Predicate<Series>, Serializable {

    @Override
    boolean test(Series series);
}

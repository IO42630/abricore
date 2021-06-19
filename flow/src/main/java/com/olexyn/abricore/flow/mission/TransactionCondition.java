package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.io.Serializable;
import java.util.function.Predicate;

@FunctionalInterface
public interface TransactionCondition extends Predicate<SnapShotSeries>, Serializable {

    @Override
    boolean test(SnapShotSeries series);
}

package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.io.Serializable;
import java.util.function.Predicate;

@FunctionalInterface
public interface TransactionCondition extends Predicate<AssetSnapshot>, Serializable {

    @Override
    boolean test(AssetSnapshot snapshot);
}

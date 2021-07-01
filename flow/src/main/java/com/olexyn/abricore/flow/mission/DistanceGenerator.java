package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.io.Serializable;
import java.util.function.Predicate;

@FunctionalInterface
public interface DistanceGenerator {

    ANum generate(Asset asset);
}

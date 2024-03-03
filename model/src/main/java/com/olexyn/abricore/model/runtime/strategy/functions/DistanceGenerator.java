package com.olexyn.abricore.model.runtime.strategy.functions;

import com.olexyn.abricore.model.runtime.snapshots.Series;

@FunctionalInterface
public interface DistanceGenerator {

    long generate(Series series);
}

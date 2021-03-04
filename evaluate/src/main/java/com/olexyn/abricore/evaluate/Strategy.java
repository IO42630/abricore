package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.util.List;
import java.util.function.Predicate;

public class Strategy {

    // TODO change this from AssetSnapshot to something more useful.
    List<Predicate<AssetSnapshot>> buyConditions;
    List<Predicate<AssetSnapshot>> sellConditions;
}

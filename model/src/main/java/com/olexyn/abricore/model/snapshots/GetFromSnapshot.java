package com.olexyn.abricore.model.snapshots;

import com.olexyn.abricore.util.ANum;

@FunctionalInterface
public interface GetFromSnapshot {

    ANum get(AssetSnapshot assetSnapshot);

}



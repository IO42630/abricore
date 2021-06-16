package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;

public class MissionUtil {

    public static boolean isMarketOpen(AssetSnapshot snapshot) {
        boolean isMarketOpenAccordingToSnapShot = snapshot.isMarketOpen();
        boolean isMarketOpenAccordingToAsset = snapshot.getAsset().isMarketOpen();
        return isMarketOpenAccordingToSnapShot && isMarketOpenAccordingToAsset;
    }
}

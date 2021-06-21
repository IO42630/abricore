package com.olexyn.abricore.fingers.paper;

import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.fingers.Navigator;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaperNavigator implements Navigator {

    private final List<SnapShotSeries> snapShotSeriesList = new ArrayList<>();
    private final Map<SnapShotSeries, Instant> counters = new HashMap<>();


    /**
     * To mimic gradual arrival of AssetSnapshot,
     * track current location in SnapShotSeries via counters.
     * If a novel SnapShotSeries is requested,
     * fetch it from store, and return the first AssetSnapshot.
     */
    @Override
    public AssetSnapshot resolveQuote(Asset asset) {
        for (SnapShotSeries snapShotSeries : snapShotSeriesList) {
           if (snapShotSeries.getAsset().equals(asset)
           ) {
               Instant lastKey = counters.get(snapShotSeries);
               Instant thisKey = snapShotSeries.higherKey(lastKey);
               counters.put(snapShotSeries, thisKey);
               return snapShotSeries.get(thisKey);
           }
        }
        SnapShotSeries snapShotSeries = SnapSeriesService.of(asset);
        snapShotSeriesList.add(snapShotSeries);
        counters.put(snapShotSeries, snapShotSeries.firstKey());
        return snapShotSeries.firstEntry().getValue();
    }

}

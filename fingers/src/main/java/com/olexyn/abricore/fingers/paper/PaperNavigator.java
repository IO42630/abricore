package com.olexyn.abricore.fingers.paper;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Navigator;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaperNavigator implements Navigator {

    private final List<Series> seriesList = new ArrayList<>();
    private final Map<Series, Instant> counters = new HashMap<>();


    /**
     * To mimic gradual arrival of AssetSnapshot,
     * track current location in Series via counters.
     * If a novel Series is requested,
     * fetch it from store, and return the first AssetSnapshot.
     */
    @Override
    public AssetSnapshot resolveQuote(Asset asset) {
        for (Series series : seriesList) {
           if (series.getAsset().equals(asset)
           ) {
               Instant lastKey = counters.get(series);
               Instant thisKey = series.higherKey(lastKey);
               counters.put(series, thisKey);
               return series.get(thisKey);
           }
        }
        Series series = SeriesService.of(asset);
        seriesList.add(series);
        counters.put(series, series.firstKey());
        return series.firstEntry().getValue();
    }

}

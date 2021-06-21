package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;

import java.util.List;
import java.util.Map;

public class DownloadTwMode extends ObserveMode {


    private TwSession twSession;
    private TwFetch twFetch;

    public DownloadTwMode(Asset asset) {
        super(asset);
    }


    public void run() throws InterruptedException {

    }

    @Override
    public void start() {
        twSession = new TwSession();
        twFetch = new TwFetch(twSession.doLogin());
    }

    @Override
    public void stop() {
        twSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException {
        Map<Asset,List<AssetSnapshot>> historicalData = twFetch.fetchHistoricalData(null);

        for (Series series : getCdfSeriesList()) {
            if (historicalData.containsKey(series.getAsset())) {
                series.addAll(historicalData.get(series.getAsset()));
            }
        }
    }

}

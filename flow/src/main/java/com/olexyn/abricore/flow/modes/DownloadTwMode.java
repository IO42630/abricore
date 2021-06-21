package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.List;
import java.util.Map;

public class DownloadTwMode extends Mode {


    private TwSession twSession;
    private TwFetch twFetch;


    @Override
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
    public void updateData() throws InterruptedException {
        Map<Asset,List<AssetSnapshot>> historicalData = twFetch.fetchHistoricalData(null);

        for (SnapShotSeries series : getSnapShotSeriesList()) {
            if (historicalData.containsKey(series.getAsset())) {
                series.addAll(historicalData.get(series.getAsset()));
            }
        }
    }

}

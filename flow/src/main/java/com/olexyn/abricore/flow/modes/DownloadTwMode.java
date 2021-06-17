package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwLogin;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

public class DownloadTwMode extends DownloadMode {


    private TwLogin twLogin;
    private TwFetch twFetch;


    @Override
    public void init() {
        twLogin = new TwLogin();
        twFetch = new TwFetch();
    }

    @Override
    public WebDriver doLogin() {
        return twLogin.doLogin();
    }

    @Override
    public void downloadHistoricalData() throws InterruptedException {
        Map<Asset,List<AssetSnapshot>> historicalData = twFetch.fetchHistoricalData(null);

        for (SnapShotSeries series : getSnapShotSeriesList()) {
            if (historicalData.containsKey(series.getAsset())) {
                series.addAll(historicalData.get(series.getAsset()));
            }
        }
    }

}

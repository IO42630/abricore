package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.datastore.TmpCsvService;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.StrategyManager;
import com.olexyn.abricore.flow.modes.observe.DownloadTwMode;
import com.olexyn.abricore.flow.modes.observe.ObserveTwMode;
import com.olexyn.abricore.flow.modes.observe.SyncCdfSqMode;
import com.olexyn.abricore.flow.modes.observe.ObserveSqMode;
import com.olexyn.abricore.flow.modes.trade.TradeSqMode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.UnderlyingAsset;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.LogUtil;
import com.olexyn.abricore.util.Param;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainApp {

    private static final Logger LOGGER = LogUtil.get(MainApp.class);

    /**
     */
    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {

        LOGGER.info("Starting the application.");
        Param.init();
        AssetService.loadAssets();
        TmpCsvService.parseTmpCsv();
        SeriesService.loadSeries(AssetService.ASSETS);


        if (Param.isEnabled("tw.download.enabled")) {
            List<Asset> assetsToDownload = AssetService.ASSETS.stream().filter(x -> x instanceof UnderlyingAsset).collect(Collectors.toList());
            new Thread(new DownloadTwMode(assetsToDownload)).start();
        }
        if (Param.isEnabled("sq.cdf.update.enabled")) {
            new Thread(new SyncCdfSqMode(setupSession())).start();
        }
        if (Param.isEnabled("tw.observe.enabled")) {
            List<Asset> assetsToObserve = new ArrayList<>();
            assetsToObserve.add(AssetService.ofName("BTCUSD"));
            assetsToObserve.add(AssetService.ofName("XAGUSD"));
            assetsToObserve.add(AssetService.ofName("AMD"));
            new Thread(new ObserveTwMode(assetsToObserve)).start();
        }
        if (Param.isEnabled("sq.observe.enabled")) {
            new Thread(new ObserveSqMode(setupSession())).start();
        }
        if (Param.isEnabled("sq.trade.enabled")) {
            new Thread(new TradeSqMode(new Mission())).start();
        }
    }


    public static Mission setupSession() {
        // strategy.buyConditions.add(x -> Cross.indicatorACrossesAboveB(
        //     x.getAsset(),
        //     n -> n.getMa().get(R5),
        //     n -> n.getMa().get(R10),
        //     Interval.H_1, x.getInstant()
        // ));
        //
        // strategy.sellConditions.add(x -> Cross.indicatorACrossesAboveB(
        //     x.getAsset(),
        //     n -> n.getMa().get(R10),
        //     n -> n.getMa().get(R5),
        //     Interval.H_1, x.getInstant()
        // ));





        Mission mission = new Mission();
        mission.setUnderlyingAsset(AssetService.ofName("XAGUSD"));


        mission.setInterval(Interval.H_1);
        mission.setStrategy(StrategyManager.setupStrategy("Test-Strategy"));
        mission.setAllocatedCapital(new ANum(1000000,0));
        return mission;
    }

}

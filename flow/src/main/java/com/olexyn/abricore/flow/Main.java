package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.datastore.TmpCsvService;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.StrategyManager;
import com.olexyn.abricore.flow.modes.observe.DownloadTwMode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.util.ANum;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {

    public static final Properties properties = new Properties();

    /**
     */
    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {

        loadProperties();

        TmpCsvService.parseTmpCsv();

        Asset underlyingAsset = AssetService.ofName("XAGUSD");

        new Thread(new DownloadTwMode(new ArrayList<>(AssetService.SYMBOLS))).start();
        // new Thread(new SyncCdfSqMode(underlyingAsset)).start();
        // new Thread(new ObserveTwMode(underlyingAsset)).start();
        // new Thread(new TradeSqMode(new Mission())).start();
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
        mission.getCdfList().addAll(List.of((Option) AssetService.ofName("XAG C 25"), (Option) AssetService.ofName("XAG C 26")));
        mission.setInterval(Interval.H_1);
        mission.setStrategy(StrategyManager.setupStrategy("Test-Strategy"));
        mission.setAllocatedCapital(new ANum(1000000,0));
        return mission;
    }

    private static void loadProperties() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("config.properties");
        if (url != null) {
            properties.load(new FileInputStream(url.getPath()));
        }
    }

}

package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.datastore.TmpCsvService;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.StrategyManager;
import com.olexyn.abricore.flow.modes.observe.DownloadTwMode;
import com.olexyn.abricore.flow.modes.observe.SyncCdfSqMode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.UnderlyingAsset;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.LogUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainApp {

    private static final Logger LOGGER = LogUtil.get(MainApp.class);

    public static final Properties config = new Properties();
    public static final Properties events = new Properties();

    /**
     */
    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {

        LOGGER.info("Starting the application.");

        loadProperties(config, "config.properties");
        loadProperties(events, "events.properties");



        TmpCsvService.parseTmpCsv();

        List<Asset> assets = AssetService.SYMBOLS.stream().filter(x -> x instanceof UnderlyingAsset).collect(Collectors.toList());
        //assets.add(AssetService.ofName("BTCUSD"));
        //assets.add(AssetService.ofName("XAGUSD"));

        new Thread(new DownloadTwMode(assets)).start();

        Thread.sleep(1000);

        new Thread(new SyncCdfSqMode(AssetService.ofName("XAGUSD"))).start();
        for (Asset asset : assets) {
           // new Thread(new SyncCdfSqMode(asset)).start();
        }

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

    public static void loadProperties(Properties properties, String fileName) throws IOException {
        String dir = System.getProperty("user.dir") + "/flow/src/main/resources/" + fileName;
        FileInputStream fis = new FileInputStream(dir);
        properties.load(fis);
        fis.close();
    }

    public static void saveProperties(Properties properties, String fileName) throws IOException {
        String dir = System.getProperty("user.dir") + "/flow/src/main/resources/" + fileName;
        FileOutputStream fos = new FileOutputStream(dir);
        properties.store(fos, "");
        fos.flush();
        fos.close();
        loadProperties(properties, fileName);
    }



}

package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.Interval;
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

        if (isEnabled("tw.download.enabled")) {
            List<Asset> assetsToDownload = AssetService.SYMBOLS.stream().filter(x -> x instanceof UnderlyingAsset).collect(Collectors.toList());
            new Thread(new DownloadTwMode(assetsToDownload)).start();
        }
        if (isEnabled("sq.cdf.update.enabled")) {
            new Thread(new SyncCdfSqMode(AssetService.ofName("XAGUSD"))).start();
        }
        if (isEnabled("tw.observe.enabled")) {
            new Thread(new ObserveTwMode(null)).start();
        }
        if (isEnabled("sq.observe.enabled")) {
            new Thread(new ObserveSqMode(AssetService.ofName("XAGUSD"))).start();
        }
        if (isEnabled("sq.trade.enabled")) {
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

    public static boolean isEnabled(String prop) {
        return Boolean.parseBoolean(config.getProperty(prop));
    }

}

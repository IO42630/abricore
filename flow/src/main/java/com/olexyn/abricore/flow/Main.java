package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.batch.ParseTmpCsvBatch;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.StrategyManager;
import com.olexyn.abricore.flow.modes.DownloadTwMode;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.flow.modes.ObserveMode;
import com.olexyn.abricore.flow.modes.ObserveTwMode;
import com.olexyn.abricore.flow.modes.TradeMode;
import com.olexyn.abricore.flow.modes.TradeSqMode;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.util.ANum;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class Main {

    public static final Properties properties = new Properties();

    /**
     */
    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {

        loadProperties();






        switch (ModeEnum.valueOf(properties.getProperty("mode"))) {
            case DOWNLOAD_TW:
                Mode downloadMode = new DownloadTwMode();
                // downloadMode.addAsset(AssetService.ofName("XAGUSD"));
                downloadMode.start();
                downloadMode.updateQuote();
                new ParseTmpCsvBatch().parseTmpCsv();
                break;
            case OBSERVE_TW:
                ObserveMode observeMode = new ObserveTwMode();
                observeMode.addAsset(AssetService.ofName("XAGUSD"));
                observeMode.run();
                break;
            case TRADE_SQ:
                TradeMode tradeMode = new TradeSqMode();
                tradeMode.run(new Mission());
                break;
            case TRAIN:
            default:
                break;
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
        mission.getDerivatives().addAll(List.of((Option) AssetService.ofName("XAG C 25"), (Option) AssetService.ofName("XAG C 26")));
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

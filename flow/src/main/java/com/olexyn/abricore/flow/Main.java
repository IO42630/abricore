package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SnapSeriesService;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.StrategyManager;
import com.olexyn.abricore.flow.modes.DownloadMode;
import com.olexyn.abricore.flow.modes.DownloadTwMode;
import com.olexyn.abricore.flow.modes.ObserveMode;
import com.olexyn.abricore.flow.modes.ObserveTwMode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.Stock;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.time.Duration;
import java.util.List;

public class Main {


    /**
     * -m mode target
     * -a asset
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        String modeEnumString = ModeEnum.OBSERVE_TW.name();
        Asset asset = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-m":
                    modeEnumString = (args[i + 1] + "_" + args[i + 2]).toUpperCase();
                case "-a":
                    asset = new Stock(args[i + 1]);
            }
        }



        switch (ModeEnum.valueOf(modeEnumString)) {
            case DOWNLOAD_TW:
                DownloadMode downloadMode = new DownloadTwMode();
                downloadMode.addAsset(AssetService.ofName("XAGUSD"));
                downloadMode.start();
                downloadMode.downloadHistoricalData();
                break;
            case OBSERVE_TW:
                ObserveMode observeMode = new ObserveTwMode();
                observeMode.addAsset(AssetService.ofName("XAGUSD"));
                observeMode.start();
                Timer timer = new Timer();
                timer.start();
                while (timer.hasPassed(Duration.ofSeconds(30))) {
                    observeMode.updateQuote();
                    Thread.sleep(1000L);
                }

                SnapShotSeries snapShotSeries = observeMode.getSnapShotSeriesList().get(0);

                SnapSeriesService.save(snapShotSeries);
                // List<Long> prices = snapShotSeries.getNavSet().stream().map(x -> snapShotSeries.get(x).getClose()).collect(Collectors.toList());
                break;
            case TRADE_SQ:
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
        mission.setAllocatedCapital(10000000L);
        return mission;
    }

}

package com.olexyn.abricore.flow;

import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.StrategyManager;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.Stock;
import com.olexyn.abricore.model.options.Option;

import java.util.List;

public class Main {


    /**
     * -m mode target
     * -a asset
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        String modeEnumString = "TRADE_SW";
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
            case COLLECT_TW:
                new CollectionMode(ModeEnum.COLLECT_TW, asset).start();
                break;
            case TRADE_SQ:
                // run trade mode
                new TradeMode().fetchLiveData();
                break;
            case TRAIN:
                // run train mode

                TrainMode trainMode = new TrainMode();
                trainMode.mission = setupSession();
                trainMode.start();

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
        mission.setUnderlyingAsset(Symbols.ofName("XAGUSD"));
        mission.getDerivatives().addAll(List.of((Option) Symbols.ofName("XAG C 25"), (Option) Symbols.ofName("XAG C 26")));
        mission.setInterval(Interval.H_1);
        mission.setStrategy(StrategyManager.setupStrategy("Test-Strategy"));
        mission.setAllocatedCapital(10000000L);
        return mission;
    }

}

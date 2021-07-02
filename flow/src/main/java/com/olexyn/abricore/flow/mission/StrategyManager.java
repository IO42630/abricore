package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.Parameters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;

/**
 * Utility to: <br>
 * - setup Strategies <br>
 * - de-/serialize Strategies <br>
 */
public class StrategyManager {

    public static Strategy setupStrategy(String name) {
        Strategy strategy = new Strategy(name);
        strategy.buyConditions.add(x -> x.getLast().getPrice().getTraded().lesser(new ANum(20000)));
        strategy.sellConditions.add(x -> x.getLast().getPrice().getTraded().greater(new ANum(27000)));
        strategy.stopConditions.add(x -> x.getLast().getPrice().getTraded().lesser(new ANum(10000)));
        strategy.sizingInCondition = x -> x.div(new ANum(5));
        strategy.sizingOutCondition = x -> x.div(new ANum(5));



        strategy.minRatio = 1d;
        strategy.maxRatio = 1d;

        strategy.setMinOptionDistance(
            asset -> {
                ANum lastUnderlyingPrice = SeriesService.getLastTraded(asset);
                return lastUnderlyingPrice.times(Objects.requireNonNull(ANum.of("0.1")));
            }
        );
        strategy.setMaxOptionDistance(
            asset -> {
                ANum lastUnderlyingPrice = SeriesService.getLastTraded(asset);
                return lastUnderlyingPrice.times(Objects.requireNonNull(ANum.of("0.2")));
            }
        );



        return strategy;
    }

    public static void writeToFile(Strategy strategy) throws IOException {
        File dataFile = new File(Parameters.STRAT_DIR_STORE + strategy.getName());
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)))) {
            out.writeObject(strategy);
        }
    }
}

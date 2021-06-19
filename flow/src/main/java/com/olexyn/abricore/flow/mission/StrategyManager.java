package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.util.Parameters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Utility to: <br>
 * - setup Strategies <br>
 * - de-/serialize Strategies <br>
 */
public class StrategyManager {

    public static Strategy setupStrategy(String name) {
        Strategy strategy = new Strategy(name);
        strategy.buyConditions.add(x -> x.getLast().getClose() < 20000);
        strategy.sellConditions.add(x -> x.getLast().getClose() > 25000);
        strategy.stopConditions.add(x -> x.getLast().getClose() < 10000);
        strategy.sizingInCondition = x -> x/5;
        strategy.sizingOutCondition = x -> x/5;
        return strategy;
    }

    public static void writeToFile(Strategy strategy) throws IOException {
        File dataFile = new File(Parameters.STRAT_DIR_STORE + strategy.getName());
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)))) {
            out.writeObject(strategy);
        }
    }
}

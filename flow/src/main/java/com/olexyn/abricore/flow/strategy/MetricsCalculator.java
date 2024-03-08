package com.olexyn.abricore.flow.strategy;

import com.olexyn.abricore.flow.jobs.paper.PaperObserveTwJob;
import com.olexyn.abricore.flow.jobs.paper.PaperTradeSqJob;
import com.olexyn.abricore.flow.jobs.util.time.PaperTimeHelper;
import com.olexyn.abricore.flow.jobs.util.time.TimeHelper;
import com.olexyn.abricore.flow.tools.OptionTools;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.navi.tw.TwNavigator;
import com.olexyn.abricore.store.dao.SnapshotDistanceDao;
import com.olexyn.abricore.store.runtime.PaperTradeService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.UuidContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.flow.jobs.JobStarter.startJob;
import static com.olexyn.abricore.util.num.Num.EP5;
import static com.olexyn.abricore.util.num.NumCalc.times;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

@Service
public class MetricsCalculator extends CtxAware {

    private static final int THREADS = 9000;


    protected MetricsCalculator(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    public void calculateFitness(List<StrategyDto> strategies) {
        List<List<StrategyDto>> splitLists = new ArrayList<>();
        List<StrategyDto> currentList = new ArrayList<>();

        for (StrategyDto strategy : strategies) {
            currentList.add(strategy);
            if (currentList.size() == THREADS) {
                splitLists.add(currentList);
                currentList = new ArrayList<>();
            }
        }

        if (!currentList.isEmpty()) {
            splitLists.add(currentList);
        }

        for (List<StrategyDto> splitList : splitLists) {
            calculateFitnessBatch(splitList);
        }
    }


    private void calculateFitnessBatch(List<StrategyDto> strategies) {
        try {
            List<Thread> simulationThreads = new ArrayList<>();
            for (var strategy : strategies) {
                simulationThreads.add(startJob(new PaperTradeSqJob(getCtx(), strategy)).getThread());
                simulationThreads.add(startJob(new PaperObserveTwJob(getCtx(), strategy)).getThread());
            }
            for (Thread thread : simulationThreads) {
                thread.join();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (var strategy : strategies) {
            strategy.getTrades().clear();
            var paperBean = getCtx()
                .getBean(UuidContext.class)
                .getBean(PaperTradeService.class, strategy.getUuid());
            if (paperBean != null) {
                var toAdd = new ArrayList<>(paperBean.getTrades().values());
                strategy.getTrades().addAll(toAdd);
            }
            cleanUp(strategy);
            long totalWin = 0;
            for (TradeDto trade : strategy.getTrades()) {
                totalWin = totalWin - times(trade.getAmount(), trade.getBuyPrice());
                if (trade.getSellPrice() != 0) {
                    totalWin = totalWin + times(trade.getAmount(), trade.getSellPrice());
                }
            }
            if (strategy.getTrades().isEmpty()) {
                totalWin = -EP5;
            }
            strategy.setFitness(totalWin);
        }
        for (var strategy : strategies) {
            getCtx().getBean(UuidContext.class).clear(strategy.getUuid());
        }
    }

    /**
     * Calculate the rating of a strategy.
     * Formula: rating = fitness * 24 / duration
     * Thus:
     * - 5 gain for 1 day would have a rating of 5.
     * - 20 gain for 1 day would have a rating of 20.
     * - 60 gain for 1 day would have a rating of 60.
     */
    private static void calcProfitByDay(List<StrategyDto> strategies) {
        strategies.forEach(strategy -> {
            int fit = toInt(strategy.getFitness());
            fit = Math.max(fit, 0);
            long hours = strategy.getDuration().toHours();
            hours = Math.max(hours, 1);
            strategy.getVector().setProfitByDay(fit * 24L / hours);
        });

    }

    /**
     * Calculate the rating of a strategy.
     * Formula: rating = 1K * fitness / volume
     * Thus:
     * - 5 gain for 1000 volume (0.5%)  would have a rating of 5.
     * - 20 gain for 1000 volume (2.0%)  would have a rating of 20.
     * - 60 gain for 1000 volume (6.0%) would have a rating of 60.
     */
    private static void calcProfitByVolume(List<StrategyDto> strategies) {
        strategies.forEach(strategy -> {
            int fit = toInt(strategy.getFitness());
            fit = Math.max(fit, 0);
            int volume = toInt(StrategyUtil.getVolume(strategy));
            volume = Math.max(volume, 1);
            long profit = 1000L * fit / volume;
            strategy.getVector().setProfitByVolume(profit);
        });

    }


    /**
     * Calculate the rating of a strategy.
     * Formula: rating = sqrt (profitByDay * profitByVolume * runningTime)
     * Thus:
     * - sqrt (   5 ( 5 profit/day) *  5 (0.5% profit/volume) * 96 (24h*4samples) ) =  34
     * - sqrt ( 200 (20 profit/day) * 20 (2.0% profit/volume) * 96 (24h*4samples) ) = 619
     */
    public static void calcRating(List<StrategyDto> strategies) {
        updateAvgDurationAndSampleCount(strategies);
        calcProfitByVolume(strategies);
        calcProfitByDay(strategies);
        strategies.forEach(strategy -> {
            var v = strategy.getVector();
            var runningTime = v.getAvgDuration() * v.getSampleCount();
            double root = Math.sqrt(v.getProfitByDay() * v.getProfitByVolume() * runningTime);
            v.setRating((long) root);
        });
    }

    private static void updateAvgDurationAndSampleCount(List<StrategyDto> strategies) {
        strategies.forEach(strategy -> {
            long oldDuration = strategy.getVector().getAvgDuration();
            long oldCount = strategy.getVector().getSampleCount();
            long newDuration = strategy.getDuration().toHours();
            long newCount = oldCount + 1;
            long newAvg = (oldDuration * oldCount + newDuration) / newCount;
            strategy.getVector().setAvgDuration(newAvg);
        });
    }


    private void cleanUp(StrategyDto strategy) {
        var uuidCtx = getCtx().getBean(UuidContext.class);
        uuidCtx.removeBean(PaperTradeService.class, strategy.getUuid());
        uuidCtx.removeBean(SeriesService.class, strategy.getUuid());
        uuidCtx.removeBean(TwNavigator.class, strategy.getUuid());
        uuidCtx.removeBean(TimeHelper.class, strategy.getUuid());
        uuidCtx.removeBean(OptionTools.class, strategy.getUuid());
        uuidCtx.removeBean(SnapshotDistanceDao.class, strategy.getUuid());
        uuidCtx.removeBean(PaperTradeService.class, strategy.getUuid());
        uuidCtx.removeBean(PaperTimeHelper.class, strategy.getUuid());
    }

}

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
     * Formula: rating = 1KK * fitness / volume
     * Thus:
     * - 5 gain for 1000 volume (0.5%)  would have a rating of 5K.
     * - 20 gain for 1000 volume (2.0%)  would have a rating of 20K.
     * - 60 gain for 1000 volume (6.0%) would have a rating of 60K.
     */
    public static void calculateRating(List<StrategyDto> strategies) {


        strategies.forEach(strategy -> {
            long dailyProfit = toInt(strategy.getFitness())
                * 24L
                / strategy.getDuration().toHours();
            strategy.getVector().setRating(dailyProfit);
        });

    }

    public static void calculateAvgDuration(List<StrategyDto> strategies) {
        strategies.forEach(strategy -> {
            strategy.getVector().setAvgDuration(strategy.getDuration().toHours());
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

package com.olexyn.abricore.flow.modes.trade;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.flow.MainApp;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.Transaction;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.LogUtil;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradeSqMode extends Mode {

    private static final Logger LOGGER = LogUtil.get(TradeSqMode.class);

    protected Mission mission;
    private Series underlyingSeries;
    protected Map<String, ANum> indicators = new HashMap<>();

    public TradeSqMode(Mission mission) {
        this.mission = mission;
        underlyingSeries = SeriesService.of(mission.getUnderlyingAsset());
    }

    @Override
    public void run() {
        SqSession.doLogin();
        SeriesService.of(mission.getUnderlyingAsset()).observers.add(this);
        while (timer.hasNotPassedSeconds(Duration.ofSeconds(Long.parseLong(MainApp.config.getProperty("run.time.seconds"))))) {
            try {
                Instant lastTwDownload = Instant.parse(MainApp.events.getProperty("tw.last.download"));
                if (lastTwDownload.plus(Duration.ofMinutes(1)).isBefore(Instant.now())) {


                    MainApp.events.setProperty("tw.last.download", Instant.now().toString());
                    MainApp.saveProperties(MainApp.events, "events.properties");
                }
                Thread.sleep(Long.parseLong(MainApp.config.getProperty("tw.download.check.interval.seconds")) * Constants.SECONDS);
            } catch (InterruptedException | IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        sleep(1000L);
        SeriesService.of(mission.getUnderlyingAsset()).observers.remove(this);
        SqSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException, IOException {

    }

    @Override
    public void onSeriesUpdate() {
        calculateIndicators();
        tryToPlaceOrders();
    }

    private void calculateIndicators() {
        indicators.put("Foo", new ANum(2));
    }

    public void tryToPlaceOrders() {
        if (checkBuyConditions().greater(new ANum(0))) {
            placeBuyOrder();
        }
        if (checkSellConditions().greater(new ANum(0))) {
            placeSellOrder();
        }
    }

    public ANum checkBuyConditions() {
        ANum result = new ANum(1);
        for (Predicate<Series> buyCondition : mission.getStrategy().buyConditions) {
            if (!buyCondition.test(underlyingSeries)) {
                result = new ANum(0);
            }
        }
        return result;
    }

    public void placeBuyOrder() {
        ANum cash = mission.getAllocatedCapital();
        ANum size = mission.getStrategy().sizingInCondition.sizeAmount(mission.getAllocatedCapital());
        ANum remainder = cash.minus(size);
        if (remainder.greater(new ANum(0,0))) {
            Transaction transaction = new Transaction(mission.getUnderlyingAsset(), Instant.now(), size, new ANum(0));
            cash = cash.minus(size);
            mission.getActiveTransactions().add(transaction);
        }
    }

    public ANum checkSellConditions() {
        ANum result = new ANum(1);
        for (Predicate<Series> sellCondition : mission.getStrategy().sellConditions) {
            if (!sellCondition.test(underlyingSeries)) {
                result = new ANum(0);
            }
        }
        return result;
    }

    public void placeSellOrder() {
        ANum cash = mission.getAllocatedCapital();
        ANum size = mission.getStrategy().sizingInCondition.sizeAmount(mission.getAllocatedCapital());
        ANum remainder = cash.minus(size);
        for (Transaction transaction : mission.getActiveTransactions()) {
            transaction.end(Instant.now(), new ANum(0));
            cash = cash.plus(transaction.getRevenue());
            mission.getFinishedTransactions().add(transaction);
        }
        mission.getActiveTransactions().removeAll(mission.getFinishedTransactions());
    }

}

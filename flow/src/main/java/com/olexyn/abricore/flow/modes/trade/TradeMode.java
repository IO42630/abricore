package com.olexyn.abricore.flow.modes.trade;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.Transaction;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class TradeMode extends Mode {

    protected Mission mission;
    protected Map<String, ANum> indicators = new HashMap<>();

    public TradeMode(Mission mission) {
        this.mission = mission;
        underlyingSeries = SeriesService.of(mission.getUnderlyingAsset());
        mission.getCdfList().forEach(this::addCdf);
    }

    @Override
    public void run() {
        start();
        SeriesService.of(mission.getUnderlyingAsset()).observers.add(this);
        sleep(1000L);
        SeriesService.of(mission.getUnderlyingAsset()).observers.remove(this);
        stop();
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

package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.Transaction;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import com.olexyn.abricore.util.ANum;

import java.time.Duration;
import java.util.function.Predicate;

public abstract class TradeMode extends Mode {

    protected Mission mission;

    public void setMission(Mission mission) {
        this.mission = mission;
        addAsset(mission.getUnderlyingAsset());
        mission.getDerivatives().forEach(this::addAsset);
    }

    public void run(Mission mission) throws InterruptedException {
        start();
        setMission(mission);
        timer.start();
        while (timer.hasPassed(Duration.ofSeconds(10))) {
            updateQuote();
            trade();
            Thread.sleep(10L);
        }
        stop();
    }

    public void trade() {
        ANum cash = mission.getAllocatedCapital();
        AssetSnapshot assetSnapshot = null;
        SnapShotSeries series = null;

        for (Predicate<SnapShotSeries> buyCondition : mission.getStrategy().buyConditions) {
            if (buyCondition.test(series)) {
                ANum size = mission.getStrategy().sizingInCondition.sizeAmount(mission.getAllocatedCapital());
                ANum remainder = cash.minus(size);
                if (remainder.greater(new ANum(0,0))) {
                    Transaction transaction = new Transaction(mission.getUnderlyingAsset(), assetSnapshot.getInstant(), size, assetSnapshot.getPrice().getTraded());
                    cash = cash.minus(size);
                    mission.getActiveTransactions().add(transaction);
                }

            }
        }

        for (Predicate<SnapShotSeries> sellCondition : mission.getStrategy().sellConditions) {
            if (sellCondition.test(series)) {
                for (Transaction transaction : mission.getActiveTransactions()) {
                    transaction.end(assetSnapshot.getInstant(), assetSnapshot.getPrice().getTraded());
                    cash = cash.plus(transaction.getRevenue());
                    mission.getFinishedTransactions().add(transaction);
                }
                mission.getActiveTransactions().removeAll(mission.getFinishedTransactions());
            }
        }
    }
}

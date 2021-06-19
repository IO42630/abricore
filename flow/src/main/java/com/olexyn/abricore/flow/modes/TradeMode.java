package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.Transaction;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.function.Predicate;

public abstract class TradeMode extends Mode {

    protected Mission mission;

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public void trade() {
        Long cash = mission.getAllocatedCapital();
        AssetSnapshot assetSnapshot = null;
        for (Predicate<AssetSnapshot> buyCondition : mission.getStrategy().buyConditions) {
            if (buyCondition.test(assetSnapshot)) {
                Long size = mission.getStrategy().sizingInCondition.sizeAmount(mission.getAllocatedCapital());
                Long remainder = cash - size;
                if (remainder > 0L) {
                    Transaction transaction = new Transaction(mission.getUnderlyingAsset(), assetSnapshot.getInstant(), size, assetSnapshot.getAverage());
                    cash = cash - size;
                    mission.getActiveTransactions().add(transaction);
                }

            }
        }
        for (Predicate<AssetSnapshot> sellCondition : mission.getStrategy().sellConditions) {
            if (sellCondition.test(assetSnapshot)) {
                for (Transaction transaction : mission.getActiveTransactions()) {
                    transaction.end(assetSnapshot.getInstant(), assetSnapshot.getAverage());
                    cash = cash + transaction.getRevenue();
                    mission.getFinishedTransactions().add(transaction);
                }
                mission.getActiveTransactions().removeAll(mission.getFinishedTransactions());
            }
        }
    }
}

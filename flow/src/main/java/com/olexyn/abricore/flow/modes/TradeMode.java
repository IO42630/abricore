package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.mission.Transaction;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.UnderlyingAsset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import com.olexyn.abricore.util.exception.UnsafeModeException;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class TradeMode extends Mode {

    protected Mission mission;

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public Asset getUnderlyingAsset() {
        List<Asset> underlyingAssetList =  getAssets().stream().filter(x -> x instanceof UnderlyingAsset).collect(Collectors.toList());
        if (underlyingAssetList.size() != 1) {
            throw new UnsafeModeException();
        } else {
            return underlyingAssetList.get(0);
        }
    }

    public void trade() {
        Long cash = mission.getAllocatedCapital();
        AssetSnapshot assetSnapshot = null;
        SnapShotSeries series = null;

        for (Predicate<SnapShotSeries> buyCondition : mission.getStrategy().buyConditions) {
            if (buyCondition.test(series)) {
                Long size = mission.getStrategy().sizingInCondition.sizeAmount(mission.getAllocatedCapital());
                Long remainder = cash - size;
                if (remainder > 0L) {
                    Transaction transaction = new Transaction(mission.getUnderlyingAsset(), assetSnapshot.getInstant(), size, assetSnapshot.getAverage());
                    cash = cash - size;
                    mission.getActiveTransactions().add(transaction);
                }

            }
        }

        for (Predicate<SnapShotSeries> sellCondition : mission.getStrategy().sellConditions) {
            if (sellCondition.test(series)) {
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

package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.util.ANum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "Trading Mission": <br>
 * - follows one Asset <br>
 * - has a fixed capital amount allocated to it <br>
 * - follows one Strategy <br>
 * - is configured and started by the MissionManager <br>
 * - can be serialized to disk in order to be analyzed
 */
public class Mission implements Serializable {

    private Asset underlyingAsset;
    private Interval interval;
    private ANum allocatedCapital;
    private Strategy strategy;

    private final List<Transaction> activeTransactions = new ArrayList<>();

    private final List<Transaction> finishedTransactions = new ArrayList<>();

    public List<Transaction> getActiveTransactions() {
        return activeTransactions;
    }

    public List<Transaction> getFinishedTransactions() {
        return finishedTransactions;
    }

    public Asset getUnderlyingAsset() {
        return underlyingAsset;
    }

    public void setUnderlyingAsset(Asset underlyingAsset) {
        this.underlyingAsset = underlyingAsset;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public ANum getAllocatedCapital() {
        return allocatedCapital;
    }

    public void setAllocatedCapital(ANum allocatedCapital) {
        this.allocatedCapital = allocatedCapital;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public ANum getProfit() {
        ANum profit = new ANum(0,0);
        for (Transaction transaction : finishedTransactions) {
            profit = profit.plus(transaction.getProfit());
        }
        return profit;
    }

    public ANum getRevenue() {
        ANum revenue = new ANum(0,0);
        for (Transaction transaction : finishedTransactions) {
            revenue = revenue.plus(transaction.getRevenue());
        }
        return revenue;
    }

    public ANum getSize() {
        ANum size = new ANum(0,0);
        for (Transaction transaction : finishedTransactions) {
            size = size.plus(transaction.getSize());
        }
        return size;
    }

    public ANum getGain() {
        return getRevenue().div(getSize());
    }


}

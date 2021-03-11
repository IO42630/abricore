package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "Trading Session": <br>
 * - follows one Asset <br>
 * - has a fixed capital amount allocated to it <br>
 * - follows one Strategy <br>
 * - is configured and started by the SessionManager <br>
 * - can be serialized to disk in order to be analyzed
 */
public class Session implements Serializable {

    private Asset asset;
    private Interval interval;
    private Long allocatedCapital;
    private Strategy strategy;

    private final List<Transaction> activeTransactions = new ArrayList<>();

    private final List<Transaction> finishedTransactions = new ArrayList<>();

    public List<Transaction> getActiveTransactions() {
        return activeTransactions;
    }

    public List<Transaction> getFinishedTransactions() {
        return finishedTransactions;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public Long getAllocatedCapital() {
        return allocatedCapital;
    }

    public void setAllocatedCapital(Long allocatedCapital) {
        this.allocatedCapital = allocatedCapital;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Long getProfit() {
        Long profit = 0L;
        for (Transaction transaction : finishedTransactions) {
            profit += transaction.getProfit();
        }
        return profit;
    }

    public Long getRevenue() {
        Long revenue = 0L;
        for (Transaction transaction : finishedTransactions) {
            revenue += transaction.getRevenue();
        }
        return revenue;
    }

    public Long getSize() {
        Long size = 0L;
        for (Transaction transaction : finishedTransactions) {
            size += transaction.getSize();
        }
        return size;
    }

    public Long getGain() {
        return getRevenue() / getSize();
    }


}

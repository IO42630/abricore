package com.olexyn.abricore.evaluate;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private final List<Transaction> activeTransactions = new ArrayList<>();

    private final List<Transaction> finishedTransactions = new ArrayList<>();

    public List<Transaction> getActiveTransactions() {
        return activeTransactions;
    }

    public List<Transaction> getFinishedTransactions() {
        return finishedTransactions;
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

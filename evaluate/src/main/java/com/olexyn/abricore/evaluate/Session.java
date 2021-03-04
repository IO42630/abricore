package com.olexyn.abricore.evaluate;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private final List<Transaction> transactions = new ArrayList<>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Long getProfit() {
        Long profit = 0L;
        for (Transaction transaction : transactions) {
            profit += transaction.getProfit();
        }
        return profit;
    }

    public Long getRevenue() {
        Long revenue = 0L;
        for (Transaction transaction : transactions) {
            revenue += transaction.getRevenue();
        }
        return revenue;
    }

    public Long getSize() {
        Long size = 0L;
        for (Transaction transaction : transactions) {
            size += transaction.getSize();
        }
        return size;
    }

    public Long getGain() {
        return getRevenue() / getSize();
    }
}

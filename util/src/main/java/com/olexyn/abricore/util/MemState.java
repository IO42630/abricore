package com.olexyn.abricore.util;

import lombok.Data;


@Data
public class MemState {


    public MemState() {
        var runtime = Runtime.getRuntime();
        this.maxGb = ((double) runtime.maxMemory() / 1024 / 1024) / 1024;
        this.totalGb = ((double) runtime.totalMemory() / 1024 / 1024) / 1024;
        this.freeGb = ((double) runtime.freeMemory() / 1024 / 1024) / 1024;
        this.usedGb = totalGb - freeGb;
    }


    private double freeGb;
    private double usedGb;
    private double totalGb;
    private double maxGb;

}

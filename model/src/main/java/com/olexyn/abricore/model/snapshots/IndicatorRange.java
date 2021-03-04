package com.olexyn.abricore.model.snapshots;

public enum IndicatorRange {
    R5(5),
    R10(10),
    R20(20),
    R50(50),
    R100(100),
    R200(200);

    private int value;

    public int getValue() {
        return this.value;
    }

    IndicatorRange(int range) {
        this.value = range;
    }
}

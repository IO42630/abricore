package com.olexyn.abricore.model.snapshots;

public enum RangeEnum {
    R5(5),
    R10(10),
    R20(20),
    R50(50),
    R100(100),
    R200(200);

    private final int num;

    public int getNum() {
        return this.num;
    }

    RangeEnum(int range) {
        this.num = range;
    }
}

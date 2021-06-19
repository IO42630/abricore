package com.olexyn.abricore.model.snapshots;



import com.olexyn.abricore.util.ANum;

public class Indicator {

    ANum r5;
    ANum r10;
    ANum r20;
    ANum r50;
    ANum r100;
    ANum r200;

    public ANum get(RangeEnum range) {
        switch (range) {
            case R5:
                return r5;
            case R10:
                return r10;
            case R20:
                return r20;
            case R50:
                return r50;
            case R100:
                return r100;
            case R200:
                return r200;
            default:
                return null;
        }
    }

    public void set(RangeEnum range, ANum value) {
        switch (range) {
            case R5:
                this.r5 = value;
                break;
            case R10:
                this.r10 = value;
                break;
            case R20:
                this.r20 = value;
                break;
            case R50:
                this.r50 = value;
                break;
            case R100:
                this.r100 = value;
                break;
            case R200:
                this.r200 = value;
                break;
        }
    }


}

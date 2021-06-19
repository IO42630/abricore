package com.olexyn.abricore.model.options;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;

public class BarrierOption extends Option {

    private ANum ratio;

    public BarrierOption(String name) {
        super(name);
    }

    public double calculatePrice(AssetSnapshot assetSnapshot) {
        return 0d;
    }


}

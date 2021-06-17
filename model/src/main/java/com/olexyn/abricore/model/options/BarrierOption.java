package com.olexyn.abricore.model.options;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;

public class BarrierOption extends Option {

    private Long ratio;

    public BarrierOption(String name) {
        super(name);
    }

    public double calculatePrice(AssetSnapshot assetSnapshot) {
        return 0d;
    }


}

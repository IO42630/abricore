package com.olexyn.abricore.model.options;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;

public class BarrierOption extends Option {

    private Long ratio;

    public BarrierOption(String name, Long ratio) {
        super(name);
        this.ratio = ratio;
    }

    public double calculatePrice(AssetSnapshot assetSnapshot) {
        return 0d;
    }

    @Override
    public Long getRatio() {
        return ratio;
    }
}

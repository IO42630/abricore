package com.olexyn.abricore.model.options;

import com.olexyn.abricore.util.ANum;

public class VanillaOption extends Option {

    public VanillaOption(String name) {
        super(name);
    }

    @Override
    public ANum getRatio() {
        return new ANum(100, 0);
    }
}

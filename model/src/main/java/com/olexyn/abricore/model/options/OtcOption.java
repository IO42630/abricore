package com.olexyn.abricore.model.options;

import com.olexyn.abricore.util.ANum;

public class OtcOption extends Option {

    private ANum ratio;

    public OtcOption(String name, ANum ratio) {
        super(name);
        this.ratio = ratio;
    }

}


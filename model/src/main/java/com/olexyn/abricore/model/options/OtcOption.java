package com.olexyn.abricore.model.options;

public class OtcOption extends Option {

    private Long ratio;

    public OtcOption(String name, Long ratio) {
        super(name);
        this.ratio = ratio;
    }

}


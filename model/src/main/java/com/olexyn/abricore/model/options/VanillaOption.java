package com.olexyn.abricore.model.options;

public class VanillaOption extends Option {

    public VanillaOption(String name) {
        super(name);
    }

    @Override
    public Long getRatio() {
        return 100L;
    }
}

package com.olexyn.abricore.util.enums;

public enum Exchange {
    SDOTS("672");


    private final String code;

    Exchange(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

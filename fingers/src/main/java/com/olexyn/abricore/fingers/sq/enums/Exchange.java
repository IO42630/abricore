package com.olexyn.abricore.fingers.sq.enums;

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

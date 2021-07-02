package com.olexyn.abricore.util.enums;

import com.olexyn.abricore.util.exception.BadDataException;

import java.util.Arrays;

public enum Exchange {
    SDOTS("672");


    private final String code;

    Exchange(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Exchange ofCode(String code) {
        return Arrays.stream(values()).filter(x -> x.getCode().equals(code)).findFirst().orElseThrow(BadDataException::new);
    }
}

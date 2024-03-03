package com.olexyn.abricore.util.enums;

import java.util.Arrays;

public enum CmdOptions {
    FORCE("-f"),
    INVALID("INVALID");

    final String code;

    CmdOptions(String code) {
        this.code = code;
    }

    public static CmdOptions ofCode(String code) {
        return Arrays.stream(values())
            .filter(x -> x.code.equals(code))
            .findFirst()
            .orElse(INVALID);
    }

}

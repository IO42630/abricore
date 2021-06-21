package com.olexyn.abricore.model.snapshots;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.COMMA;

public enum Header {
    TIME,
    PRICE_TRADED,
    PRICE_BID,
    PRICE_ASK,
    VOLUME;

    public static String getHeader() {
        return Arrays.stream(Header.values()).map(Enum::name).collect(Collectors.joining(COMMA)) + "\n";
    }
}

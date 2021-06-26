package com.olexyn.abricore.datastore;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.COMMA;

/**
 * The COLUMNS which are tracked when storing CSV locally.
 */
public enum StoreHeader {
    TIME,
    PRICE_TRADED,
    PRICE_BID,
    PRICE_ASK,
    RANGE,
    VOLUME;

    public static String getHeader() {
        return Arrays.stream(StoreHeader.values()).map(Enum::name).collect(Collectors.joining(COMMA)) + "\n";
    }
}

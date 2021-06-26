package com.olexyn.abricore.datastore;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.COMMA;

/**
 * The COLUMNS which are tracked when downloading CSV from external sources.
 */
public enum TmpHeader {
    TIME,
    OPEN,
    LOW,
    CLOSE,
    VOLUME;

    public static String getHeader() {
        return Arrays.stream(TmpHeader.values()).map(Enum::name).collect(Collectors.joining(COMMA)) + "\n";
    }
}

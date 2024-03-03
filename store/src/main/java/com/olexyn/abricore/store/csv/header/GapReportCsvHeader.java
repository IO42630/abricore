package com.olexyn.abricore.store.csv.header;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.COMMA;

/**
 * The COLUMNS of the GapReport
 */
public enum GapReportCsvHeader {
    ASSET,
    START,
    END,
    TYPE,
    SIZE;

    public static String getHeader() {
        return Arrays.stream(GapReportCsvHeader.values()).map(Enum::name)
            .collect(Collectors.joining(COMMA)) + "\n";
    }
}

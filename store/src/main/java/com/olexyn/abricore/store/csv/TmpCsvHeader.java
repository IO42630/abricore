package com.olexyn.abricore.store.csv;

import java.util.Arrays;
import java.util.List;

/**
 * The COLUMNS which are tracked when downloading CSV from external sources.
 */
public enum TmpCsvHeader {
    TIME,
    OPEN,
    HIGH,
    LOW,
    VOLUME;

    public static List<String> getHeader() {
        return Arrays.stream(TmpCsvHeader.values()).map(Enum::name).toList();
    }
}

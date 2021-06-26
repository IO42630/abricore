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
    HIGH,
    LOW,
    VOLUME;
}

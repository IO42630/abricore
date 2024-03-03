package com.olexyn.abricore.model.runtime.assets;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;

import static com.olexyn.abricore.util.Constants.EMPTY;

public enum AssetType {
    COMMODITY(EMPTY),
    STOCK(EMPTY),
    BARRIER_OPTION("2200"),
    ETF(EMPTY),
    CRYPTO(EMPTY),
    CASH(EMPTY),
    UNKNOWN(EMPTY);

    @Getter
    private final String sqCode;

    public static @Nullable AssetType ofSqCode(String sqCode) {
        return Arrays.stream(values())
            .filter(x -> x.getSqCode().equals(sqCode))
            .findFirst().orElse(null);
    }

    AssetType(String sqCode) {
        this.sqCode = sqCode;
    }

}

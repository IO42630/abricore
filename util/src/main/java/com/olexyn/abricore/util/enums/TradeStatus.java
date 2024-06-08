package com.olexyn.abricore.util.enums;

import java.util.List;

public enum TradeStatus {
    OPEN_ISSUED,
    OPEN_PENDING,
    OPEN_EXECUTED,
    CLOSE_ISSUED,
    CLOSE_PENDING,
    CLOSE_EXECUTED;


    /**
     * ACTIVE means part of the current portfolio.
     */
    public static final List<TradeStatus> ACTIVE_POS = List.of(
        OPEN_EXECUTED,
        CLOSE_ISSUED,
        CLOSE_PENDING
    );



    /**
     * LATENT means potentially part of the current portfolio. E.g.:
     * - open pending
     * - cancel open, pending
     * - active
     * - close pending
     */
    public static final List<TradeStatus> LATENT_POS = List.of(
        OPEN_ISSUED,
        OPEN_PENDING,
        OPEN_EXECUTED,
        CLOSE_ISSUED,
        CLOSE_PENDING
    );

    public static final List<TradeStatus> OPENING_POS = List.of(
        OPEN_ISSUED,
        OPEN_PENDING
    );

}

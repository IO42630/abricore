package com.olexyn.abricore.flow;

import java.util.Arrays;
import java.util.List;

public enum JobType {

    OBS_POS_SQ("obs-pos-sq"),
    DL_TW("dl-tw"),
    READ_TO_DB("read-to-db"),
    SYNC_OPTIONS_SQ("sync-opt-sq"),
    OBS_TW("obs-tw"),
    PAPER_OBS_TW("paper-obs-tw"),
    TRADE_SQ("trade-sq"),
    PAPER_TRADE_SQ("paper-trade-sq"),
    EVOLVE("evolve"),
    FIX_NULL_VALUES_DB("fix-null-values-db"),
    UNKNOWN("unknown");

    private final String command;

    JobType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static JobType of(String jobStr) {
        return Arrays.stream(values())
            .filter(x -> x.getCommand().equals(jobStr))
            .findFirst().orElse(UNKNOWN);
    }

    public static List<JobType> validValues() {
        return Arrays.stream(values())
            .filter(x -> x != UNKNOWN)
            .toList();
    }

}

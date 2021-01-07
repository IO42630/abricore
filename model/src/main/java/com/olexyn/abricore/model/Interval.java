package com.olexyn.abricore.model;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;

public enum Interval {
    S_1(Duration.ofSeconds(1)),
    S_5(Duration.ofSeconds(5)),
    S_15(Duration.ofSeconds(15)),
    S_30(Duration.ofSeconds(30)),
    M_1(Duration.ofMinutes(1)),
    M_3(Duration.ofMinutes(3)),
    M_5(Duration.ofMinutes(5)),
    M_15(Duration.ofMinutes(15)),
    M_30(Duration.ofMinutes(30)),
    M_45(Duration.ofMinutes(45)),
    H_1(Duration.ofHours(1)),
    H_2(Duration.ofHours(2)),
    H_3(Duration.ofHours(3)),
    H_4(Duration.ofHours(4)),
    D_1(Period.ofDays(1)),
    W_1(Period.ofWeeks(1)),
    MONTH_1(Period.ofMonths(1)),
    Y_1(Period.ofYears(1));

    public final TemporalAmount size;

    Interval(TemporalAmount size) {
        this.size = size;
    }
}


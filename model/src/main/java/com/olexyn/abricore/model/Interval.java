package com.olexyn.abricore.model;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;

public enum Interval {
    S_1(Duration.ofSeconds(1), "1 second"),
    S_5(Duration.ofSeconds(5), "5 seconds"),
    S_15(Duration.ofSeconds(15), "15 seconds"),
    S_30(Duration.ofSeconds(30), "30 seconds"),
    M_1(Duration.ofMinutes(1), "1 minute"),
    M_3(Duration.ofMinutes(3), "3 minutes"),
    M_5(Duration.ofMinutes(5), "5 minutes"),
    M_15(Duration.ofMinutes(15), "15 minutes"),
    M_30(Duration.ofMinutes(30), "30 minutes"),
    M_45(Duration.ofMinutes(45), "45 minutes"),
    H_1(Duration.ofHours(1), "1 hour"),
    H_2(Duration.ofHours(2), "2 hours"),
    H_3(Duration.ofHours(3), "3 hours"),
    H_4(Duration.ofHours(4), "4 hours"),
    D_1(Period.ofDays(1), "1 day"),
    W_1(Period.ofWeeks(1), "1 week"),
    MONTH_1(Period.ofMonths(1), "1 month"),
    Y_1(Period.ofYears(1), "");

    public final TemporalAmount size;
    private final String twLabel;

    public String getTwLabel(){
        return  twLabel;
    }

    Interval(TemporalAmount size, String twLabel) {
        this.size = size;
        this.twLabel = twLabel;
    }
}


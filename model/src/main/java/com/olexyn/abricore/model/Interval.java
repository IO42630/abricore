package com.olexyn.abricore.model;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public enum Interval {
    S_1(Duration.ofSeconds(1), "1 second", "567"),
    S_5(Duration.ofSeconds(5), "5 seconds", "567"),
    S_15(Duration.ofSeconds(15), "15 seconds", "567"),
    S_30(Duration.ofSeconds(30), "30 seconds", "30"),
    M_1(Duration.ofMinutes(1), "1 minute", "60"),
    M_3(Duration.ofMinutes(3), "3 minutes", "567"),
    M_5(Duration.ofMinutes(5), "5 minutes", "567"),
    M_15(Duration.ofMinutes(15), "15 minutes", "567"),
    M_30(Duration.ofMinutes(30), "30 minutes", "567"),
    M_45(Duration.ofMinutes(45), "45 minutes", "567"),
    H_1(Duration.ofHours(1), "1 hour", "567"),
    H_2(Duration.ofHours(2), "2 hours", "567"),
    H_3(Duration.ofHours(3), "3 hours", "567"),
    H_4(Duration.ofHours(4), "4 hours", "567"),
    D_1(Period.ofDays(1), "1 day", "567"),
    W_1(Period.ofWeeks(1), "1 week", "567"),
    MONTH_1(Period.ofMonths(1), "1 month", "567"),
    Y_1(Period.ofYears(1), "", "567");

    public final TemporalAmount size;
    private final String twLabel;
    private final String fileLabel;

    public String getTwLabel(){
        return  twLabel;
    }

    public String getFileLabel() { return  fileLabel;}

    Interval(TemporalAmount size, String twLabel, String fileLabel) {
        this.size = size;
        this.twLabel = twLabel;
        this.fileLabel = fileLabel;
    }

    public static Set<String> getFileLabels() {
        return Arrays.stream(Interval.values()).map(Interval::getFileLabel).collect(Collectors.toSet());
    }
}


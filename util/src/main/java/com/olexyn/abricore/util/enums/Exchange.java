package com.olexyn.abricore.util.enums;

import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.exception.StoreException;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;

@Getter
public enum Exchange {


    SDOTS("672", DataUtil.parseTime("08:00:00"), DataUtil.parseTime("22:00:00"), Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)),
    EUWAX("521", DataUtil.parseTime("08:00:00"), DataUtil.parseTime("22:00:00")),
    SIX("4"),
    NASDAQ("67"),
    NYSE("65"),
    OTHER_INDEXES("M8"),
    FX("10M", DataUtil.parseTime("08:00:00"), DataUtil.parseTime("22:00:00")); // COMMODITIES
    // For now we don't differentiate between SDOTS and the underlying FX in terms of trading hours.
    // This is because if the SDOTS is closed, we can't trade the underying FX anyways.




    private final String code;
    private final LocalTime open;
    private final LocalTime close;
    private final Set<DayOfWeek> days = new HashSet<>();

    /**
     * We don't trade these.
     */
    Exchange(String code) {
        this.code = code;
        this.open = MAX;
        this.close = MIN;
    }

    Exchange(String code, LocalTime open, LocalTime close) {
        this.code = code;
        this.open = open;
        this.close = close;
    }

    Exchange(String code, LocalTime open, LocalTime close, Set<DayOfWeek> days) {
        this.code = code;
        this.open = open;
        this.close = close;
        this.days.addAll(days);
    }

    public static Exchange ofCode(String code) {
        return Arrays.stream(values())
            .filter(x -> x.getCode().equals(code))
            .findFirst()
            .orElseThrow(StoreException::new);
    }

    public LocalTime getOpen(DayOfWeek dayOfWeek) {
        if (getDays().contains(dayOfWeek)) {
            return MAX;
        }
        return open;
    }

    public LocalTime getClose(DayOfWeek dayOfWeek) {
        if (getDays().contains(dayOfWeek)) {
            return MIN;
        }
        return close;
    }

}

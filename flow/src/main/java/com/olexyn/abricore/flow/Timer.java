package com.olexyn.abricore.flow;

import java.time.Instant;
import java.time.temporal.TemporalAmount;

public class Timer {

    private Instant startTime;

    void start() {
        startTime = Instant.now();
    }

    boolean hasPassed(TemporalAmount duration) {
        return startTime.plus(duration).isAfter(Instant.now());
    }

}

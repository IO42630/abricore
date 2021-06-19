package com.olexyn.abricore.flow;

import java.time.Instant;
import java.time.temporal.TemporalAmount;

public class Timer {

    private Instant startTime;

    public void start() {
        startTime = Instant.now();
    }

    public boolean hasPassed(TemporalAmount duration) {
        return startTime.plus(duration).isAfter(Instant.now());
    }

}

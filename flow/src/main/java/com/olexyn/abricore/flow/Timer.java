package com.olexyn.abricore.flow;

import com.olexyn.abricore.util.Constants;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

public class Timer {

    private Instant startTime;

    public void start() {
        startTime = Instant.now();
    }

    public boolean hasNotPassedSeconds(TemporalAmount duration) {
        return startTime.plus(duration).isAfter(Instant.now());
    }

    public boolean hasNotPassedSeconds(String prop) {
        return hasNotPassedSeconds(Duration.ofSeconds(Long.parseLong(MainApp.config.getProperty(prop))));
    }

    public void sleepMilli(String prop) throws InterruptedException {
        Thread.sleep(Long.parseLong(MainApp.config.getProperty(prop)));
    }

    public void sleepSeconds(String prop) throws InterruptedException {
        Thread.sleep(Long.parseLong(MainApp.config.getProperty(prop)) * Constants.SECONDS);
    }

    public void sleepMinutes(String prop) throws InterruptedException {
        Thread.sleep(Long.parseLong(MainApp.config.getProperty(prop)) * Constants.SECONDS * Constants.MINUTES);
    }

    public void sleepHours(String prop) throws InterruptedException {
        Thread.sleep(Long.parseLong(MainApp.config.getProperty(prop)) * Constants.SECONDS * Constants.MINUTES * Constants.HOURS);
    }

}

package com.olexyn.abricore.util.enums;

import lombok.Getter;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.S1;
import static com.olexyn.abricore.util.Constants.S15;

@Getter
public enum Interval {
    S_1(S1, "1 second", "1S"),
    S_5(Duration.ofSeconds(5), "5 seconds", "5S"),
    S_15(S15, "15 seconds", "15S"),
    S_30(Duration.ofSeconds(30), "30 seconds", "30S"),
    M_1(Duration.ofMinutes(1), "1 minute", "1"),
    M_3(Duration.ofMinutes(3), "3 minutes", "3"),
    M_5(Duration.ofMinutes(5), "5 minutes", "5"),
    M_15(Duration.ofMinutes(15), "15 minutes", "15"),
    M_30(Duration.ofMinutes(30), "30 minutes", "30"),
    M_45(Duration.ofMinutes(45), "45 minutes", "45"),
    H_1(Duration.ofHours(1), "1 hour", "60"),
    H_2(Duration.ofHours(2), "2 hours", "120"),
    H_3(Duration.ofHours(3), "3 hours", "180"),
    H_4(Duration.ofHours(4), "4 hours", "240");

    private final Duration duration;
    private final String twLabel;
    private final String fileToken;

    Interval(Duration duration, String twLabel, String fileToken) {
        this.duration = duration;
        this.twLabel = twLabel;
        this.fileToken = fileToken;
    }

    public static Set<String> getFileTokens() {
        return Arrays.stream(Interval.values())
            .map(Interval::getFileToken)
            .collect(Collectors.toSet());
    }

    public static Interval ofFileToken(String token) {
        return Arrays.stream(values())
            .filter(interval -> interval.fileToken.equals(token))
            .findAny().orElseThrow();
    }
}


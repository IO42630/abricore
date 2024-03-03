package com.olexyn.abricore.flow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TaskType {

    GAP_REPORT("gap-report"),
    VECTOR_MERGE("vector-merge"),
    VECTOR_NULL("vector-null"),
    VECTOR_DEFRAG("vector-defrag"),
    VECTOR_SAVE("vector-save"),
    UNKNOWN("unknown");

    private final String command;

    TaskType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static TaskType of(String command) {
        return Arrays.stream(values())
            .filter(x -> x.getCommand().equals(command))
            .findFirst().orElse(UNKNOWN);
    }

    public static List<TaskType> validValues() {
        return Arrays.stream(values())
            .filter(x -> x != UNKNOWN)
            .collect(Collectors.toList());
    }

}

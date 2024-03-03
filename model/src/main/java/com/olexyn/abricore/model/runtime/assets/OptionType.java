package com.olexyn.abricore.model.runtime.assets;

import java.util.stream.Stream;

public enum OptionType {
    CALL,
    PUT;

    public static Stream<OptionType> stream() {
        return Stream.of(OptionType.values());
    }

}

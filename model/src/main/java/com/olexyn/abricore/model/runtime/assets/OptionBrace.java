package com.olexyn.abricore.model.runtime.assets;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.Nullable;

@Getter
@Setter
public class OptionBrace {

    private OptionDto call = null;

    private OptionDto put = null;

    public OptionBrace(@Nullable OptionDto call, @Nullable OptionDto put) {
        if (call != null) { this.call = call; }
        if (put != null) { this.put = put; }
    }

    public OptionDto get(OptionType optionType) {
        return switch (optionType) {
            case CALL -> getCall();
            case PUT -> getPut();
            default -> throw new IllegalArgumentException();
        };
    }

}

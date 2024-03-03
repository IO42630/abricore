package com.olexyn.abricore.util.enums;

public enum OptionStatus {
    FOUND,
    KNOWN,
    SELECTED_FOR_BRACE,
    DEAD /* DEAD also includes "not found", i.e. if for some reason the Option can't be found in SQ. */
}

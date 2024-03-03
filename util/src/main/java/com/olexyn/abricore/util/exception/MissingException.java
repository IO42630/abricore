package com.olexyn.abricore.util.exception;

import java.io.Serial;

/**
 * Something is missing.
 * Usually, this is a required data element.
 * This is meant as a "not-so-serious" exception.
 */
public class MissingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7190479309944308613L;

    public MissingException() {
        super();
    }

    public MissingException(String message) {
        super(message);
    }
}

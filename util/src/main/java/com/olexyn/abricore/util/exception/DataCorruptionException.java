package com.olexyn.abricore.util.exception;

import java.io.Serial;

/**
 * The operation to be performed would probably corrupt data.
 */
public class DataCorruptionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6507603881868599720L;

    public DataCorruptionException() {
        super();
    }

    public DataCorruptionException(String message) {
        super(message);
    }
}

package com.olexyn.abricore.util.exception;


/**
 * Was trying to do something dumb, like dividing by zero.
 */
public class SoftCalcException extends RuntimeException {

    public SoftCalcException() {
        super();
    }

    public SoftCalcException(String message) {
        super(message);
    }
}

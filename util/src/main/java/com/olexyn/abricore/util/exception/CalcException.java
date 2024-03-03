package com.olexyn.abricore.util.exception;

import java.io.Serial;

/**
 * Something critical went wrong when performing calculations.
 */
public class CalcException extends Exception {
    @Serial
    private static final long serialVersionUID = 7662231145232146803L;


    public CalcException() {
        super();
        printStackTrace();
    }

    public CalcException(String message) {
        super(message);
        printStackTrace();
    }

}

package org.cuba.neofit.exceptions;

import org.cuba.exceptions.CubaException;

public class NeofitException extends CubaException {
    private static final long serialVersionUID = -3778929741870825258L;

    public NeofitException() {

    }

    public NeofitException(String message) {
        super(message);
    }

    public NeofitException(Throwable cause) {
        super(cause);
    }

    public NeofitException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeofitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

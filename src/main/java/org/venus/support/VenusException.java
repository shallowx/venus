package org.venus.support;

import java.io.Serial;

public class VenusException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4731708405228264641L;

    public VenusException() {
    }

    public VenusException(String message) {
        super(message);
    }

    public VenusException(String message, Throwable cause) {
        super(message, cause);
    }

    public VenusException(Throwable cause) {
        super(cause);
    }

    public VenusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

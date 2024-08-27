package org.venus.support;

import java.io.Serial;

public class VenusAdminException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4731708405228264641L;

    public VenusAdminException() {
    }

    public VenusAdminException(String message) {
        super(message);
    }

    public VenusAdminException(String message, Throwable cause) {
        super(message, cause);
    }

    public VenusAdminException(Throwable cause) {
        super(cause);
    }

    public VenusAdminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

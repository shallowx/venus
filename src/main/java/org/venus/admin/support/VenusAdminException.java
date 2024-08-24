package org.venus.admin.support;

public class VenusAdminException extends RuntimeException {

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

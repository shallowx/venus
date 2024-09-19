package org.venus.support;

import java.io.Serial;

/**
 * The VenusException class extends RuntimeException and represents a specific type of
 * unchecked exception that can be thrown during the normal operation of the Java Virtual Machine.
 *
 * This exception can serve as a base class for more specific runtime exceptions that can
 * occur in the application.
 */
public class VenusException extends RuntimeException {
    /**
     * A unique identifier for serialization.
     *
     * This `serialVersionUID` is used to ensure that a serialized object
     * can be properly deserialized by matching the version of the class
     * used during serialization with the version of the class used during
     * deserialization. If the versions do not match,
     * an `InvalidClassException` is thrown.
     */
    @Serial
    private static final long serialVersionUID = -4731708405228264641L;

    /**
     * Constructs a new VenusException with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link Throwable#initCause}.
     */
    public VenusException() {
    }

    /**
     * Constructs a new VenusException with the specified detail message.
     *
     * @param message the detail message, which is saved for later retrieval by the getMessage() method.
     */
    public VenusException(String message) {
        super(message);
    }

    /**
     * Constructs a new VenusException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public VenusException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new VenusException with the specified cause.
     *
     * @param cause the cause of this exception, which can be retrieved later
     *              using the {@link Throwable#getCause()} method.
     */
    public VenusException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new VenusException with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message           the detail message.
     * @param cause             the cause of the exception.
     * @param enableSuppression whether or not suppression is enabled or disabled.
     * @param writableStackTrace whether or not the stack trace should be writable.
     */
    public VenusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

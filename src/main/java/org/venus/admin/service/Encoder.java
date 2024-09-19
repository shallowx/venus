package org.venus.admin.service;

/**
 * This is an interface for encoding long values into a specific string representation.
 * Implementations of this interface will provide various encoding mechanisms.
 */
public interface Encoder {
    /**
     * Encodes a given long value into a specific string representation.
     *
     * @param code the long value to be encoded.
     * @return the encoded string representation of the input.
     */
    String encode(long code);
}

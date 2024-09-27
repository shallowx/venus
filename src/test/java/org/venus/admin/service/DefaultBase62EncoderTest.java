package org.venus.admin.service;

import org.junit.jupiter.api.Test;
import org.venus.support.VenusBase62Encoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class is used to test the DefaultBase62Encoder.java class,
 * specifically the encode method which encodes the input long value to a base62 encoded String.
 */
public class DefaultBase62EncoderTest {

    // Instance of class to test
    private VenusBase62Encoder encoder = VenusBase62Encoder.INSTANCE;

    @Test
    public void testEncodeWithZeroInput() {
        // Zero should be encoded as "A" since "A" is the first character in the base62 encoding space
        assertEquals("A", encoder.encode(0));
    }

    @Test
    public void testEncodeWithPositiveInput() {
        long input = 62;
        // 62 should be encoded to "B" since "B" is the second character in the base62 encoding space,
        // and the encoding system starts from zero index.
        assertEquals("B", encoder.encode(input));
    }

    @Test
    public void testEncodeWithLargeInput() {
        long input = 1357986420;
        // the result is derived from the encode function
        assertEquals("3CLbFw", encoder.encode(input));
    }
}
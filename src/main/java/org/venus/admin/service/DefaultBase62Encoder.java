package org.venus.admin.service;

import lombok.extern.slf4j.Slf4j;

/**
 * DefaultBase62Encoder is an implementation of the Encoder interface that provides
 * base62 encoding for long values. It represents a singleton instance of the encoder
 * with predefined base62 characters, including [0-9], [a-z], and [A-Z].
 *
 * It contains methods and properties necessary to perform encoding of long numbers
 * into a base62 string representation.
 */
@Slf4j
public class DefaultBase62Encoder implements Encoder {
    /**
     * Singleton instance of DefaultBase62Encoder.
     *
     * This instance can be used to encode long values into base62 encoded strings using
     * the predefined base62 characters [0-9], [a-z], and [A-Z]. It ensures a single,
     * globally accessible instance of the encoder.
     */
    public static final DefaultBase62Encoder INSTANCE = new DefaultBase62Encoder();

    /**
     * ENCODE_CHARS is a character array containing the predefined set of
     * characters used for base62 encoding, including uppercase letters
     * [A-Z], lowercase letters [a-z], and digits [0-9].
     */
    private static final char[] ENCODE_CHARS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    /**
     * Constant that defines the number of encoded characters available.
     * It is derived from the length of the character array {@code ENCODE_CHARS},
     * which includes characters used for base62 encoding.
     */
    private static final int SPEED = ENCODE_CHARS.length;

    /**
     * Encodes a given long input into a base62 string representation.
     *
     * @param input the long value to be encoded.
     * @return the base62 encoded string representation of the input.
     */
    @Override
    public String encode(long input) {
        var encode = new StringBuilder();
        if (input == 0) {
            return String.valueOf(ENCODE_CHARS[0]);
        }

        while (input > 0) {
            encode.append(ENCODE_CHARS[(int) (input % SPEED)]);
            input = input / SPEED;
        }
        return encode.reverse().toString();
    }
}

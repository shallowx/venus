package org.venus.support;

import lombok.extern.slf4j.Slf4j;
import org.venus.admin.service.Encoder;

/**
 * DefaultBase62Encoder is an implementation of the Encoder interface that provides
 * base62 encoding for long values. It represents a singleton instance of the encoder
 * with predefined base62 characters, including [0-9], [a-z], and [A-Z].
 *
 * It contains methods and properties necessary to perform encoding of long numbers
 * into a base62 string representation.
 */
@Slf4j
public class Base64Encoder implements Encoder {
    /**
     * Singleton instance of DefaultBase62Encoder.
     *
     * This instance can be used to encode long values into base62 encoded strings using
     * the predefined base62 characters [0-9], [a-z], and [A-Z]. It ensures a single,
     * globally accessible instance of the encoder.
     */
    public static final Base64Encoder INSTANCE = new Base64Encoder();

    /**
     * Creates a private constructor for the DefaultBase62Encoder class.
     *
     * This constructor is private to enforce the singleton pattern, ensuring that only one
     * instance of DefaultBase62Encoder can be created. It restricts instantiation from
     * outside the class, allowing only the provided static instance to be used.
     */
    private Base64Encoder() {
    }

    /**
     * ENCODE_CHARS is a character array containing the predefined set of
     * characters used for base62 encoding, including uppercase letters
     * [A-Z], lowercase letters [a-z], and digits [0-9].
     */
    private static final char[] ENCODE_CHARS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+','-'
    };
    /**
     * Constant that defines the number of encoded characters available.
     * It is derived from the length of the character array {@code ENCODE_CHARS},
     * which includes characters used for base62 encoding.
     */
    private static final int SPEED = ENCODE_CHARS.length;
    /**
     * A bitmask that is derived from the number of available encoded characters.
     * This mask is used for bitwise operations during encoding processes.
     * The value is calculated as {@code SPEED} minus one, where {@code SPEED} represents
     * the total number of characters used for base62 encoding.
     */
    private static final int MASK = SPEED - 1;

    /**
     * Encodes a given long value into a base62 encoded string.
     * This method converts the input long value into a string
     * representation using base62 characters (0-9, a-z, A-Z).
     *
     * @param input The long value to be encoded.
     * @return A base62 encoded string representing the input value.
     */
    @Override
    public String encode(long input) {
        if (input == 0) {
            return String.valueOf(ENCODE_CHARS[0]);
        }
        int estimatedCapacity = (int) (Math.log(input) / Math.log(SPEED)) + 1;
        var encode = new StringBuilder(estimatedCapacity);
        while (input > 0) {
            encode.append(ENCODE_CHARS[(int) (input & MASK)]);
            input >>= 6;
        }
        return encode.reverse().toString();
    }
}

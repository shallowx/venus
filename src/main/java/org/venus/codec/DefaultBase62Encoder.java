package org.venus.codec;

import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Slf4j
public class DefaultBase62Encoder implements Encoder{
    public static final DefaultBase62Encoder INSTANCE = new DefaultBase62Encoder();

    private static final char[] ENCODE_CHARS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    @Override
    public String encode(long code) {
        return "";
    }

    @Override
    public long decode(String s) {
        return 0;
    }
}

package org.venus.codec;

public interface Encoder {
    String encode(long code);
    long decode(String s);
}

package org.venus.cache;

public interface Callback {
    void callback(String key, Object o, String type);
}

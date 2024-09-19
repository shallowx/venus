package org.venus.cache;

/**
 * The Callback interface provides a mechanism to handle callbacks
 * in the context of multi-level caching events. It is used to perform
 * specific actions when cache-related operations such as updates or
 * evictions occur.
 */
public interface Callback {
    /**
     * This method is invoked as a callback during cache operations such as updates or evictions.
     * It allows users to define custom behavior that should occur when specific cache events happen.
     *
     * @param key The cache key which is being updated or evicted.
     * @param o The object associated with the cache key; it could be null in case of eviction.
     * @param type The type of the operation being performed, e.g., "update" or "evict".
     */
    void callback(String key, Object o, String type);
}

package org.venus.cache;

/**
 * Specifies the type of cache message listener.
 *
 * The {@code CacheMessageListenerType} enum is used to indicate the type of action to be performed
 * on the cache in response to a message. It can be either {@code UPDATE} to update the cache with
 * a new value or {@code INVALIDATE} to remove the cache entry.
 */
public enum CacheMessageListenerType {
    UPDATE, INVALIDATE
}

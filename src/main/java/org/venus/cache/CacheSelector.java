package org.venus.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Interface providing access to primary and secondary cache instances.
 * Used for managing different levels of caching mechanisms.
 */
public interface CacheSelector {
    /**
     * Returns a primary cache with String keys and Object values.
     *
     * @return a Cache instance configured to store and retrieve values using String keys and Object values
     */
    // Returns a primary cache with String keys and Object values
    Cache<String, Object> primaryCache();

    /**
     * Returns a RedisTemplate with String keys and CacheWrapper values.
     *
     * @return a RedisTemplate configured to manage caches with String keys and CacheWrapper values
     */
    // Returns a RedisTemplate with String keys and CacheWrapper values
    RedisTemplate<String, CacheWrapper> secondCache();
}

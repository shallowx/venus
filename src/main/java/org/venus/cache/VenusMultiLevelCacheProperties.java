package org.venus.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class holds the configuration properties for the Venus multi-level cache system.
 * It includes settings for both the in-memory and remote (i.e., Redis) caching layers.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.venus.multi.level.cache")
public class VenusMultiLevelCacheProperties {
    /**
     * Determines whether null values are allowed in the cache.
     * When true, the cache can store null values. When false, null values are not permitted.
     */
    private boolean allowNull = true;
    /**
     * The initial capacity of the multi-level cache.
     * This determines how many elements the cache can initially hold.
     */
    private int initCapacity;
    /**
     * Defines the maximum capacity for the multi-level cache.
     * By default, this is set to the minimum representable integer value.
     */
    private int maxCapacity;
    /**
     * Specifies the duration in milliseconds after which a cache entry should expire
     * since the last time it was written.
     */
    private long expireAfterWrite;
    /**
     * Specifies the duration (in milliseconds) after which an entry should be expired
     * if it has not been accessed. This property is used to control the lifespan of cache
     * entries based on their access patterns.
     */
    private long expireAfterAccess;
    /**
     * Specifies the expiration time for cached items in Redis in milliseconds.
     *
     * This value determines how long an item remains in the Redis cache before
     * it is considered expired and eligible for removal.
     */
    private long redisExpires;
    /**
     * Defines the count of redis keys to be scanned in a single iteration.
     * This value is used to control the breadth and performance of scan operations
     * within a Redis database, allowing for optimized memory and processing efficiency.
     */
    private long redisScanCount;
}

package org.venus.cache;

import java.lang.annotation.*;

/**
 * Represents an annotation for defining a multi-level cache.
 *
 * Attributes:
 * - cacheName: Specifies the name of the cache.
 * - key: Specifies the key for the cache.
 * - type: Specifies the type of cache operation, with a default value of ALL (covers all operations).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// This annotation is used to define a multi-level cache
public @interface VenusMultiLevelCache {
    /**
     * Specifies the name of the cache.
     *
     * @return The name of the cache.
     */
    String cacheName();

    /**
     * Specifies the key for the cache.
     *
     * @return the key of the cache
     */
    String key();

    /**
     * Specifies the type of cache operation to be used in the multi-level cache.
     *
     * @return the type of cache operation, with a default value of ALL
     */
    MultiLevelCacheType type() default MultiLevelCacheType.ALL;
}
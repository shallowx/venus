package org.venus.cache;

/**
 * MultiLevelCacheType defines types of operations for multi-level caching.
 * It is used to specify different cache behaviors namely: ALL, PUT, and EVICT.
 *
 * - ALL: Indicates all cache operations (put and evict).
 * - PUT: Indicates that only put operations in the cache should be considered.
 * - EVICT: Indicates that only evict operations in the cache should be considered.
 */
public enum MultiLevelCacheType {
    ALL,
    PUT,
    EVICT
}

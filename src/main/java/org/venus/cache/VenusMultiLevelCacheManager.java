package org.venus.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages multi-level cache integrating local and remote caches.
 * This class implements the CacheManager interface to provide a structured
 * approach for handling cache instances.
 */
public class VenusMultiLevelCacheManager implements CacheManager {
    /**
     * A concurrent hash map used to store and manage cache instances.
     * The key is a string representing the cache name, and the value is the corresponding Cache object.
     */
    private final Map<String, Cache> caches = new ConcurrentHashMap<>();
    /**
     * Configuration properties for Venus Multi-Level Cache.
     * Provides the configuration necessary for setting up the multi-level caching system
     * integrating both local and remote cache mechanisms.
     */
    private final VenusMultiLevelCacheProperties properties;
    /**
     * The second level cache implemented using Redis.
     * This cache acts as the remote cache layer, extending the caching mechanism beyond the local in-memory cache.
     * It uses {@code RedisTemplate<String, CacheWrapper>} to interact with Redis, where each cache entry is
     * wrapped in a {@code CacheWrapper} object that holds the key-value pair.
     */
    private final RedisTemplate<String, CacheWrapper> secondCache;
    /**
     * The primary local-level cache using Caffeine as the caching library.
     * This cache is designed to store in-memory data for fast access,
     * reducing the need to fetch frequently accessed data from remote caches.
     */
    private final com.github.benmanes.caffeine.cache.Cache<String, Object> primaryCache;

    /**
     * Constructs a new VenusMultiLevelCacheManager with the specified properties and Redis template.
     *
     * @param properties the properties used to configure the multi-level caching system
     * @param secondCache the RedisTemplate instance used for operations on the remote cache
     */
    public VenusMultiLevelCacheManager(VenusMultiLevelCacheProperties properties, RedisTemplate<String, CacheWrapper> secondCache) {
        this.properties = properties;
        this.secondCache = secondCache;
        this.primaryCache = buildCaffeineCache();
    }

    /**
     * Retrieves the cache associated with the given name. If the cache does not exist,
     * it creates a new multi-level cache using the provided name and configuration properties.
     *
     * @param name the name of the cache to retrieve
     * @return the cache instance associated with the given name
     */
    @Override
    public Cache getCache(@NonNull String name) {
        Cache cache = caches.get(name);
        if (cache != null) {
            return cache;
        }
        return caches.computeIfAbsent(name, s -> new VenusMultiLevelValueAdaptingCache(name, secondCache, primaryCache, properties));
    }

    /**
     * Builds and configures a Caffeine cache instance based on the properties specified
     * in the VenusMultiLevelCacheProperties instance.
     *
     * @return a configured Caffeine cache instance with the provided settings for initial capacity,
     *         maximum size, and expiration policies for access and write.
     */
    private com.github.benmanes.caffeine.cache.Cache<String, Object> buildCaffeineCache() {
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder();
        Optional<VenusMultiLevelCacheProperties> opt = Optional.ofNullable(this.properties);
        opt.map(VenusMultiLevelCacheProperties::getInitCapacity)
                .ifPresent(caffeineBuilder::initialCapacity);
        opt.map(VenusMultiLevelCacheProperties::getMaxCapacity)
                .ifPresent(caffeineBuilder::maximumSize);
        opt.map(VenusMultiLevelCacheProperties::getExpireAfterAccess)
                .ifPresent(eaa -> caffeineBuilder.expireAfterAccess(eaa, TimeUnit.MILLISECONDS));
        opt.map(VenusMultiLevelCacheProperties::getExpireAfterWrite)
                .ifPresent(eaa -> caffeineBuilder.expireAfterWrite(eaa, TimeUnit.MILLISECONDS));
        return caffeineBuilder.build();
    }

    /**
     * Retrieves the names of all available caches managed by this CacheManager.
     *
     * @return A collection of strings representing the names of the caches.
     */
    @NonNull
    @Override
    public Collection<String> getCacheNames() {
        return caches.keySet();
    }
}

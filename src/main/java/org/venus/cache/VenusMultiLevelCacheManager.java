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

public class VenusMultiLevelCacheManager implements CacheManager, CacheSelector {
   private final Map<String, Cache> caches = new ConcurrentHashMap<>();
   private final VenusMultiLevelCacheProperties properties;
   private final RedisTemplate<String, CacheWrapper> secondCache;
   private final com.github.benmanes.caffeine.cache.Cache<String, CacheWrapper> primaryCache;

    public VenusMultiLevelCacheManager(VenusMultiLevelCacheProperties properties, RedisTemplate<String, CacheWrapper> secondCache) {
        this.properties = properties;
        this.secondCache = secondCache;
        this.primaryCache = buildCaffeineCache();
    }

    @Override
    public Cache getCache(@NonNull String name) {
        Cache cache = caches.get(name);
        if (cache != null) {
            return cache;
        }
        return caches.computeIfAbsent(name, s -> new VenusMultiLevelValueAdaptingCache(name,secondCache, primaryCache, properties));
    }

    private com.github.benmanes.caffeine.cache.Cache<String, CacheWrapper> buildCaffeineCache(){
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder();
        Optional<VenusMultiLevelCacheProperties> opt = Optional.ofNullable(this.properties);
        opt.map(VenusMultiLevelCacheProperties::getInitCapacity)
                .ifPresent(caffeineBuilder::initialCapacity);
        opt.map(VenusMultiLevelCacheProperties::getMaxCapacity)
                .ifPresent(caffeineBuilder::maximumSize);
        opt.map(VenusMultiLevelCacheProperties::getExpireAfterAccess)
                .ifPresent(eaa->caffeineBuilder.expireAfterAccess(eaa,TimeUnit.MILLISECONDS));
        opt.map(VenusMultiLevelCacheProperties::getExpireAfterWrite)
                .ifPresent(eaa->caffeineBuilder.expireAfterWrite(eaa,TimeUnit.MILLISECONDS));
        return caffeineBuilder.build();
    }

    @NonNull
    @Override
    public Collection<String> getCacheNames() {
        return caches.keySet();
    }

    @Override
    public com.github.benmanes.caffeine.cache.Cache<String, CacheWrapper> primaryCache() {
        return this.primaryCache;
    }
    @SuppressWarnings("all")
    @Override
    public RedisTemplate<String, CacheWrapper> secondCache() {
        return this.secondCache();
    }
}

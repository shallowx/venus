package org.venus.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

public interface CacheSelector {
    Cache<String, CacheWrapper> primaryCache();
    RedisTemplate<String, CacheWrapper> secondCache();
}

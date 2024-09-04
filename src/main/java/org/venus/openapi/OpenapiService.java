package org.venus.openapi;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.venus.cache.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.venus.cache.VenusMultiLevelCacheConstants.REDIRECT_CACHE_NAME;
import static org.venus.cache.VenusMultiLevelCacheConstants.VENUS_CACHE_CALLBACK_NAME;

@Service
@Slf4j
public class OpenapiService implements IOpenapiService, Callback {

    private final OpenapiRepository openapiRepository;
    private final VenusMultiLevelCacheManager manager;
    private final OpenapiCacheConsistentAlarm alarm;
    private final OpenapiInitializerProperties properties;
    private final RedisTemplate<String, CacheWrapper> redisTemplate;
    private static final String VENUS_INITIALIZER_KV = "venus-initializer-kv";

    @Autowired
    public OpenapiService(OpenapiRepository openapiRepository, VenusMultiLevelCacheManager manager, OpenapiInitializerProperties properties,
                          ObjectProvider<OpenapiCacheConsistentAlarm> provider, RedisTemplate<String, CacheWrapper> redisTemplate) {
        this.openapiRepository = openapiRepository;
        this.manager = manager;
        this.properties = properties;
        this.redisTemplate = redisTemplate;
        this.alarm = provider.getIfAvailable();
    }

    @PostConstruct
    public void init() {
        if (!properties.isInitialized()) {
            if (log.isWarnEnabled()) {
                log.warn("Venus redis initialize not need");
            }
            return;
        }

        // Prevent restarts or expansions from causing the service to load initialization data multiple times
        // And under normal circumstances, there is no need to set the expiration time for the key or delete the key
        // If the pod starts abnormally, you can choose to manually delete and change the key, or accept the method of initializing the cache when accessing the key to initialize the data
        Boolean isSuccess = redisTemplate.opsForValue().setIfAbsent(VENUS_INITIALIZER_KV, CacheWrapper.builder()
                .key(VENUS_INITIALIZER_KV).value(VENUS_INITIALIZER_KV)
                .build());
        if (Boolean.FALSE.equals(isSuccess)) {
            if (log.isInfoEnabled()) {
                log.info("venus redirect initializer was successfully");
            }
            return;
        }

        List<OpenapiEntity> entities = openapiRepository.lists();
        if (entities == null || entities.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("Venus redis initialize from db is empty");
            }
            return;
        }

        Cache cache = manager.getCache(REDIRECT_CACHE_NAME);
        for (OpenapiEntity entity : entities) {
            try {
                cache.put(entity.getCode(), entity);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Venus redis key[{}] initialize was failure with entity[{}]", entity.getCode(), entity, e);
                }
            }
        }
    }

    @VenusMultiLevelCache(cacheName = REDIRECT_CACHE_NAME, key = "#encode", type = MultiLevelCacheType.ALL)
    @Override
    public OpenapiEntity get(String encode) {
        return openapiRepository.get(encode);
    }


    @Override
    public List<OpenapiEntity> lists() {
        return openapiRepository.lists().stream()
                .filter(f -> f.getIsActive() != 0 && f.getExpiresAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @VenusMultiLevelCache(cacheName = REDIRECT_CACHE_NAME, key = "#encode", type = MultiLevelCacheType.ALL)
    @Override
    public OpenapiEntity redirect(String encode) {
        OpenapiEntity entity = this.get(encode);
        if (entity == null
                || entity.getIsActive() == 0
                || entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }
        return entity;
    }

    /**
     * If the caller needs to cache the response, and it needs to register a callback method to update the new data from venus.
     * Register method see 'admin(package)' function. when venus cache was updated, then the method will call back that register api
     * to notify caller to update redirect url, e.g.
     */
    @Override
    public void callback(String key, Object o, String type) {
        CallbackRequestEntity entity = CallbackRequestEntity.builder().key(key).o(o).type(type).build();
        try {
            CacheSelector selector = (VenusMultiLevelValueAdaptingCache) manager.getCache(REDIRECT_CACHE_NAME);
            selector.secondCache().convertAndSend(VENUS_CACHE_CALLBACK_NAME, entity);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Venus cache update callback failure", e);
            }
            alarm.alarm(key, o, type);
        }
    }

    @Getter
    @Setter
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    static class CallbackRequestEntity implements Serializable {
        @Serial
        private static final long serialVersionUID = 2717227104050710204L;
        private String key;
        private Object o;
        private String type; // update or evict
    }
}

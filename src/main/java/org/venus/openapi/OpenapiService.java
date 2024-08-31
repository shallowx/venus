package org.venus.openapi;

import jakarta.annotation.PostConstruct;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.venus.cache.VenusMultiLevelCacheConstants.REDIRECT_CACHE_NAME;
import static org.venus.cache.VenusMultiLevelCacheConstants.VENUS_CACHE_CALLBACK_NAME;

@Service
@Slf4j
public class OpenapiService implements IOpenapiService, Callback {

    private final OpenapiRepository openapiRepository;
    private final VenusMultiLevelCacheManager manager;
    private final OpenapiCacheConsistentAlarm alarm;

    @Autowired
    public OpenapiService(OpenapiRepository openapiRepository, VenusMultiLevelCacheManager manager, ObjectProvider<OpenapiCacheConsistentAlarm> provider) {
        this.openapiRepository = openapiRepository;
        this.manager = manager;
        this.alarm = provider.getIfAvailable();
    }

    @PostConstruct
    public void init() {
        CacheSelector selector = (VenusMultiLevelCacheManager) manager.getCache(REDIRECT_CACHE_NAME);
        RedisTemplate<String, CacheWrapper> template = selector.secondCache();
        try {
            // It is only used when the service starts, because the distributed lock implementation is reasonable.
            Boolean isLocked = template.opsForValue().setIfPresent("venus-initializer-distributed-lock", CacheWrapper.builder()
                    .key("venus-initializer-distributed-lock")
                    .value("venus-initializer-distributed-lock")
                    .build(), Duration.ofMillis(5 * 60 * 1000L));
            if (Boolean.FALSE.equals(isLocked)) {
                if (log.isDebugEnabled()) {
                    log.debug("Venus redis was initialized");
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
                    cache.put(entity.getCode(), CacheWrapper.builder().key(entity.getCode()).value(entity).build());
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Venus redis key[{}] initialize was failure with entity[{}]", entity.getCode(), entity, e);
                    }
                }
            }
        } finally {
            template.delete("venus-initializer-distributed-lock");
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
            manager.secondCache().convertAndSend(VENUS_CACHE_CALLBACK_NAME, entity);
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

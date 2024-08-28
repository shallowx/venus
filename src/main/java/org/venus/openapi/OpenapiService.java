package org.venus.openapi;

import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.venus.cache.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.venus.cache.VenusMultiLevelCacheConstants.*;

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
        if (log.isInfoEnabled()) {
            log.info("Venus cache fill is starting");
        }

        Cache cache = manager.getCache(REDIRECT_CACHE_NAME);
        if (cache == null) {
            if (log.isWarnEnabled()) {
                log.warn("Venus cache is NULL, cache will close");
            }
            return;
        }
        List<OpenapiEntity> entities = this.openapiRepository.lists();
        if (entities == null || entities.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Venus url mapping data is empty");
            }
            return;
        }

        for (OpenapiEntity entity : entities) {
            short isActive = entity.getIsActive();
            if (isActive == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("{} is not active", entity);
                }
                continue;
            }

            LocalDateTime expiresAt = entity.getExpiresAt();
            if (expiresAt.isBefore(LocalDateTime.now())) {
                if (log.isDebugEnabled()) {
                    log.debug("{} is expires before now", entity);
                }
                continue;
            }
            cache.put(entity.getCode(), CacheWrapper.builder().value(entity).key(entity.getCode()).build());
        }

        if (log.isInfoEnabled()) {
            log.info("Venus cache fill was ended");
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

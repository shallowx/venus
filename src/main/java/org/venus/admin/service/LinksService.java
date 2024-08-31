package org.venus.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.domain.LinksEntity;
import org.venus.admin.repository.LinksRepository;
import org.venus.openapi.OpenapiEntity;

import java.util.List;

import static org.venus.cache.VenusMultiLevelCacheConstants.DEFAULT_LISTENER_NAME;

@Service
@Slf4j
public class LinksService implements ILinksService {

    @Autowired
    private LinksRepository linksRepository;
    @Autowired
    private CacheManager cacheManager;

    @Override
    public List<LinksEntity> lists() {
        return linksRepository.list();
    }

    @Override
    public LinksEntity get(long id) {
        return linksRepository.get(id);
    }

    @Override
    public void add(LinksDao ld) {
        linksRepository.add(ld);
        Cache cache = cacheManager.getCache(DEFAULT_LISTENER_NAME);
        if (cache != null) {
            cache.put(ld.getCode(), OpenapiEntity.from(ld));
        }
    }

    @Override
    public void update(LinksDao ld) {
        linksRepository.update(ld);
        Cache cache = cacheManager.getCache(DEFAULT_LISTENER_NAME);
        if (cache != null) {
            cache.put(ld.getCode(), OpenapiEntity.from(ld));
        }
    }

    @Override
    public void delete(long id) {
        LinksEntity entity = this.get(id);
        linksRepository.remove(id);
        Cache cache = cacheManager.getCache(DEFAULT_LISTENER_NAME);
        if (cache != null) {
            cache.evict(entity.getCode());
        }
    }
}

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
import java.util.concurrent.*;

import static org.venus.cache.VenusMultiLevelCacheConstants.VENUS_REDIRECT_CACHE_NAME;

/**
 * Service class implementing the ILinksService interface.
 * Provides methods for managing links, including listing, retrieving, adding, updating, and deleting link entries.
 * Utilizes a LinksRepository for database operations and a CacheManager for caching link data.
 */
@Service
@Slf4j
public class LinksService implements ILinksService {

    /**
     * Autowired repository for performing CRUD operations on LinksEntity.
     */
    @Autowired
    private LinksRepository linksRepository;
    /**
     * A CacheManager instance used to manage caching operations within the LinksService.
     * This component is responsible for accessing, storing, and evicting cache entries,
     * which helps in improving the performance of the service by reducing database access.
     * It is particularly used for caching link data identified by unique codes, supporting
     * the retrieval and management of links with reduced latency.
     */
    @Autowired
    private CacheManager cacheManager;

    /**
     * A ScheduledExecutorService designed to handle retry logic for updating the cache in case of errors.
     * This executor uses a single virtual thread, named "retry-update-cache-with-error",
     * to ensure retry operations are performed with minimal resource overhead.
     */
    private static final ScheduledThreadPoolExecutor retryUpdateCacheIfErrorExecutor = (ScheduledThreadPoolExecutor)Executors.newFixedThreadPool(1, Thread.ofVirtual().name("retry-update-cache-with-error").factory());

    /**
     * Retrieves a list of all link entities from the system.
     *
     * @return A list containing all instances of {@link LinksEntity}.
     */
    @Override
    public List<LinksEntity> lists() {
        return linksRepository.list();
    }

    /**
     * Retrieves a link entity based on its unique identifier.
     *
     * @param id the unique identifier of the link to be retrieved
     * @return the LinksEntity associated with the given id
     */
    @Override
    public LinksEntity get(long id) {
        return linksRepository.get(id);
    }

    /**
     * Adds a new link represented by the given LinksDao object to the repository and updates the cache.
     *
     * @param ld The LinksDao object containing data about the link to be added.
     */
    @Override
    public void add(LinksDao ld) {
        linksRepository.add(ld);
        try {
            Cache cache = cacheManager.getCache(VENUS_REDIRECT_CACHE_NAME);
            if (cache != null) {
                cache.put(ld.getCode(), OpenapiEntity.from(ld));
            }
        }catch (Exception e) {
            log.error("Error while adding links to the cache", e);
            Runnable task = () -> {
                Cache cache = cacheManager.getCache(VENUS_REDIRECT_CACHE_NAME);
                if (cache != null) {
                    cache.put(ld.getCode(), OpenapiEntity.from(ld));
                }
            };
            try {
                retryUpdateCacheIfErrorExecutor.scheduleAtFixedRate(task, 0, 5000, TimeUnit.MILLISECONDS);
            }catch (Exception ex) {
                log.error("Error while retry adding links to the cache", ex);
                if (!retryUpdateCacheIfErrorExecutor.isShutdown()) {
                    // Discard previous tasks
                    BlockingQueue<Runnable> scheduledPoolQueue = retryUpdateCacheIfErrorExecutor.getQueue();
                    if (!scheduledPoolQueue.isEmpty()) {
                        scheduledPoolQueue.clear();
                    }
                    retryUpdateCacheIfErrorExecutor.scheduleAtFixedRate(task, 0, 5000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    /**
     * Updates an existing link entry in the database and updates the cache with the new link data.
     *
     * @param ld The LinksDao object containing updated data for the link to be modified.
     */
    @Override
    public void update(LinksDao ld) {
        linksRepository.update(ld);
        try {
            Cache cache = cacheManager.getCache(VENUS_REDIRECT_CACHE_NAME);
            if (cache != null) {
                cache.put(ld.getCode(), OpenapiEntity.from(ld));
            }
        } catch (Exception e) {
            log.error("Error while updating links to the cache", e);
            Runnable task = () -> {
                Cache cache = cacheManager.getCache(VENUS_REDIRECT_CACHE_NAME);
                if (cache != null) {
                    cache.put(ld.getCode(), OpenapiEntity.from(ld));
                }
            };
            try {
                retryUpdateCacheIfErrorExecutor.scheduleAtFixedRate(task, 0, 5000, TimeUnit.MILLISECONDS);
            }catch (Exception ex) {
                log.error("Error while retry updating links to the cache", ex);
                if (!retryUpdateCacheIfErrorExecutor.isShutdown()) {
                    // Discard previous tasks
                    BlockingQueue<Runnable> scheduledPoolQueue = retryUpdateCacheIfErrorExecutor.getQueue();
                    if (!scheduledPoolQueue.isEmpty()) {
                        scheduledPoolQueue.clear();
                    }
                    retryUpdateCacheIfErrorExecutor.scheduleAtFixedRate(task, 0, 5000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    /**
     * Deletes a link entity from the system by its unique identifier. This method performs the following actions:
     * - Retrieves the link entity associated with the given id.
     * - Removes the link entity from the database.
     * - Evicts the corresponding entry from the cache if present.
     *
     * @param id The unique identifier of the link entity to be deleted.
     */
    @Override
    public void delete(long id) {
        LinksEntity entity = this.get(id);
        linksRepository.remove(id);
        try {
            Cache cache = cacheManager.getCache(VENUS_REDIRECT_CACHE_NAME);
            if (cache != null) {
                cache.evict(entity.getCode());
            }
        } catch (Exception e) {
            log.error("Error while deleting links from the cache", e);
            Runnable task = () -> {
                Cache cache = cacheManager.getCache(VENUS_REDIRECT_CACHE_NAME);
                if (cache != null) {
                    cache.evict(entity.getCode());
                }
            };
            try {
                retryUpdateCacheIfErrorExecutor.scheduleAtFixedRate(task, 0, 5000, TimeUnit.MILLISECONDS);
            }catch (Exception ex) {
                log.error("Error while retry deleting links from the cache", ex);
                if (!retryUpdateCacheIfErrorExecutor.isShutdown()) {
                    // Discard previous tasks
                    BlockingQueue<Runnable> scheduledPoolQueue = retryUpdateCacheIfErrorExecutor.getQueue();
                    if (!scheduledPoolQueue.isEmpty()) {
                        scheduledPoolQueue.clear();
                    }
                    retryUpdateCacheIfErrorExecutor.scheduleAtFixedRate(task, 0, 5000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}

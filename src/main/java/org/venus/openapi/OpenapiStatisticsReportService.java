package org.venus.openapi;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service for reporting and managing OpenAPI statistics.
 * Implements the methods defined in the IOpenapiStatisticsReportService interface.
 */
@Service
@Slf4j
public class OpenapiStatisticsReportService implements IOpenapiStatisticsReportService {
    /**
     * Repository for performing CRUD operations and custom queries on
     * OpenAPI statistics data.
     *
     * The {@code openapiStatisticsRepository} is a repository interface
     * that extends a Spring Data repository, providing methods to interact
     * with the database for managing OpenAPI statistics.
     *
     * This repository is automatically injected by Spring's dependency
     * injection mechanism using the {@code @Autowired} annotation.
     */
    @Autowired
    private OpenapiStatisticsRepository openapiStatisticsRepository;
    /**
     * This variable holds the configuration properties for generating reports using the OpenAPI specification.
     * It is automatically injected by the Spring framework through dependency injection.
     */
    @Autowired
    private OpenapiReportProperties reportProperties;
    /**
     * A list of OpenapiStatisticsEntity objects.
     * This list is initialized as a LinkedList and is immutable.
     */
    private final List<OpenapiStatisticsEntity> entities = new LinkedList<>();
    /**
     * This variable holds the timestamp of the last update.
     * It is initialized to the current system time in milliseconds at the instance creation.
     */
    private long lastUpdatedTime = System.currentTimeMillis();
    /**
     * A reentrant read-write lock that provides separate lock modes for reading and writing operations.
     * Read locks can be held simultaneously by multiple threads as long as no write lock is held.
     * The write lock is exclusive, ensuring that only one thread can modify the shared resource at a time.
     *
     * This lock is generally preferable to a plain {@code synchronized} block when you have a resource
     * that is frequently read but infrequently written.
     *
     * The lock can be used to improve concurrency and performance in scenarios where read operations
     * significantly outnumber write operations.
     *
     * Here, `readWriteLock` creates an instance of {@code ReentrantReadWriteLock} to manage access to
     * code blocks or resources that require thread-safe read and/or write operations.
     */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * Write lock associated with the ReentrantReadWriteLock instance.
     * This lock is used to ensure that only one thread can perform write operations
     * at a given time, while allowing multiple threads to concurrently read when
     * no write operations are taking place.
     */
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    /**
     * The read lock component of a {@code ReentrantReadWriteLock} instance.
     *
     * This lock is used to control read access to a shared resource, allowing
     * multiple threads to acquire the read lock concurrently as long as no
     * thread holds the write lock.
     *
     * By invoking the {@code readLock} method on the associated {@code ReentrantReadWriteLock},
     * this read lock instance is acquired. Acquiring the read lock for reading
     * ensures that while the read lock is held, the write lock cannot be obtained,
     * thus preventing modifications to the shared resource by other threads.
     */
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    /**
     * A ScheduledExecutorService instance for managing and scheduling tasks
     * to execute after a specified delay or periodically.
     * This service provides methods to schedule commands to run after a
     * certain delay or to execute periodically.
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Processes and reports the provided OpenapiStatisticsEntity.
     *
     * @param entity the OpenapiStatisticsEntity object to be processed and reported
     * @return true if the entity was successfully processed and reported, false otherwise
     */
    @Override
    public boolean report(OpenapiStatisticsEntity entity) {
       return this.write(entity);
    }

    /**
     * Initializes the statistics report scheduler. This method is annotated with {@code @PostConstruct},
     * indicating it is called after the dependency injection is done to perform any initialization.
     * It sets up a scheduled task to periodically check and report statistics based on the
     * {@link OpenapiReportProperties.StatisticsReportProperties} configuration.
     *
     * The scheduled task will trigger at a fixed rate, controlled by the {@code scheduledDelay} and
     * {@code scheduledPeriod} properties. Within the task, if the size of the {@code entities} exceeds
     * the {@code reportSize} or the time since the last update surpasses the {@code reportTimeout},
     * the entities are read and cleared, and saved to {@code openapiStatisticsRepository}.
     *
     * The task will sleep for a duration specified by {@code reportWithoutDataTimeout} if there is
     * no data to report. If any exception occurs during the execution, an error message will be logged.
     */
    @PostConstruct
    public void init() {
        OpenapiReportProperties.StatisticsReportProperties statisticsReportProperties = reportProperties.getStatistics();
        ThreadFactory factory = Thread.ofVirtual().name("statistics-report-virtual-thread").factory();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(factory);
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (true) {
                try {
                    int size = entities.size();
                    if (size > statisticsReportProperties.getReportSize() ||
                            System.currentTimeMillis() - lastUpdatedTime > statisticsReportProperties.getReportTimeout()) {
                        List<OpenapiStatisticsEntity> newEntities = readAndClear();
                        if (newEntities == null || newEntities.isEmpty()) {
                            continue;
                        }
                        lastUpdatedTime = System.currentTimeMillis();
                        openapiStatisticsRepository.saveAll(newEntities);
                    }

                    TimeUnit.MILLISECONDS.sleep(statisticsReportProperties.getReportWithoutDataTimeout());
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Statistics report failure", e);
                    }
                }
            }
        }, statisticsReportProperties.getScheduledDelay(), statisticsReportProperties.getScheduledPeriod(), TimeUnit.MILLISECONDS);
    }

    /**
     * Reads and returns the current list of OpenapiStatisticsEntity objects, then clears the internal list.
     *
     * @return A List containing the OpenapiStatisticsEntity objects that were present before the internal list was cleared.
     */
    private List<OpenapiStatisticsEntity> readAndClear() {
        writeLock.lock();
        List<OpenapiStatisticsEntity> newEntities;
        try {
            newEntities = new ArrayList<>(entities);
            entities.clear();
        } finally {
            writeLock.unlock();
        }
        return newEntities;
    }

    /**
     * Writes the given OpenapiStatisticsEntity to the end of the entities list.
     *
     * @param entity the OpenapiStatisticsEntity to be written
     * @return true if the entity is successfully written, false otherwise
     */
    private boolean write(OpenapiStatisticsEntity entity) {
        readLock.lock();
        try {
            entities.addLast(entity);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Write statistics report failure", e);
            }
            return false;
        }finally {
            readLock.unlock();
        }
        return true;
    }

    /**
     * Shuts down the scheduled executor service if it is not null.
     * This method is annotated with @PreDestroy, indicating that it will be
     * called before the instance is destroyed, typically as part of the
     * application's shutdown sequence.
     */
    @PreDestroy
    public void shutdown() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
        }
    }
}

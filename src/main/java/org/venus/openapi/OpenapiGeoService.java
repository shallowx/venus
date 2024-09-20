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
 * Service responsible for handling geographic data reports.
 * <p>
 * This class implements the {@link IOpenapiGeoService} interface for reporting geographic data
 * to a repository. It manages scheduled tasks for collecting and saving geographic data, ensuring
 * that data is saved based on specified time intervals or data size thresholds.
 */
@Service
@Slf4j
public class OpenapiGeoService implements IOpenapiGeoService {
    /**
     * An instance of the OpenapiGeoRepository that is automatically
     * injected by the Spring framework.
     *
     * This repository interface facilitates CRUD operations and other
     * interactions with the geographical data persisted in the
     * underlying data store.
     *
     * The OpenapiGeoRepository is used to manage and query geographical
     * information in accordance with the application's requirements.
     *
     * Being marked with @Autowired, the instance will be resolved from
     * the application context and injected into the dependent component
     * where it is used.
     */
    @Autowired
    private OpenapiGeoRepository openapiGeoRepository;
    /**
     * Holds the configuration properties required to generate reports.
     * This property is automatically injected.
     */
    @Autowired
    private OpenapiReportProperties reportProperties;
    /**
     * A list that holds OpenapiGeoEntity objects.
     * It is initialized as a LinkedList and is immutable.
     */
    private final List<OpenapiGeoEntity> entities = new LinkedList<>();
    /**
     * Stores the timestamp of the last update. The value is initialized to the current system time
     * when the instance is created. This variable may be used to track the relevance or freshness
     * of the data.
     */
    private long lastUpdatedTime = System.currentTimeMillis();
    /**
     * This is a ReentrantReadWriteLock used to control read/write access to a shared resource.
     * ReentrantReadWriteLock allows multiple readers to acquire the lock concurrently as long as
     * no writers hold the lock, and it allows a single writer to acquire the lock if no readers are holding it.
     * This lock provides better performance in scenarios where reads are frequent and writes are infrequent.
     */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * A write lock used to enforce exclusive access to resources or critical sections.
     * This lock is part of a ReentrantReadWriteLock mechanism, which allows multiple
     * threads to read simultaneously while requiring exclusive access for write operations.
     * The write lock ensures that only one thread can hold the lock at a time, preventing
     * data inconsistency and ensuring thread safety for write operations.
     */
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    /**
     * The read lock component of a ReentrantReadWriteLock.
     *
     * This lock is used to allow multiple threads to read a shared resource concurrently,
     * ensuring that no thread can modify the resource while it is being read.
     *
     * The readLock instance is obtained from a ReentrantReadWriteLock, which manages both
     * the read and write locks for a resource, coordinating access to ensure thread-safety.
     *
     * Locking the readLock will only succeed if no thread currently holds the write lock.
     *
     * Typical usage involves acquiring the read lock before accessing the shared resource
     * for reading, and releasing it afterward.
     */
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    /**
     * A scheduled thread pool executor that can schedule commands to run after a
     * given delay or to execute periodically. The ScheduledExecutorService
     * interface in the java.util.concurrent package is useful for tasks that need
     * to be executed in parallel and those that should be scheduled to run at a
     * specific time or interval.
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Reports the given OpenapiGeoEntity by writing it to the designated output.
     *
     * @param entity the OpenapiGeoEntity object to be reported
     */
    @Override
    public void report(OpenapiGeoEntity entity) {
        write(entity);
    }

    /**
     * Initializes the Geo reporting service. This method sets up a scheduled task
     * using a virtual thread factory that runs at fixed intervals to check the size
     * and age of collected geo entities. If the entities exceed a configured size
     * or age, they are persisted to the repository. The scheduled task handles
     * errors by logging them.
     *
     * The task periodically sleeps for a configured duration when there is no data
     * to report.
     *
     * The schedule is configured with an initial delay and period from the
     * GeoReportProperties.
     *
     * This method is annotated with {@code @PostConstruct} to ensure it is called
     * after the bean's properties have been initialized.
     */
    @PostConstruct
    public void init() {
        OpenapiReportProperties.GeoReportProperties geoReportProperties = reportProperties.getGeo();
        ThreadFactory factory = Thread.ofVirtual().name("geo-report-virtual-thread").factory();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(factory);
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (true) {
                try {
                    int size = entities.size();
                    if (size > geoReportProperties.getReportSize() ||
                            System.currentTimeMillis() - lastUpdatedTime > geoReportProperties.getReportTimeout()) {
                        List<OpenapiGeoEntity> newEntities = readAndClear();
                        if (newEntities == null || newEntities.isEmpty()) {
                            continue;
                        }
                        lastUpdatedTime = System.currentTimeMillis();
                        openapiGeoRepository.saveAll(newEntities);
                    }

                    TimeUnit.MILLISECONDS.sleep(geoReportProperties.getReportWithoutDataTimeout());
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Geo report failure", e);
                    }
                }
            }
        }, geoReportProperties.getScheduledDelay(), geoReportProperties.getScheduledPeriod(), TimeUnit.MILLISECONDS);
    }

    /**
     * Reads the current list of OpenapiGeoEntity objects and clears the list simultaneously.
     * This method safely locks the list during the read and clear operations to maintain thread safety.
     *
     * @return a new list containing all the OpenapiGeoEntity objects that were present before clearing the original list.
     */
    private List<OpenapiGeoEntity> readAndClear() {
        writeLock.lock();
        List<OpenapiGeoEntity> newEntities;
        try {
            newEntities = new ArrayList<>(entities);
            entities.clear();
        } finally {
            writeLock.unlock();
        }
        return newEntities;
    }

    /**
     * Adds the provided OpenapiGeoEntity to the end of the entities list in a thread-safe manner.
     *
     * @param entity the OpenapiGeoEntity to be added to the entities list
     */
    private void write(OpenapiGeoEntity entity) {
        readLock.lock();
        try {
            entities.addLast(entity);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Shuts down the ScheduledExecutorService if it is not null.
     * This method is annotated with @PreDestroy to ensure that it is called
     * during the instance's destruction lifecycle, facilitating a clean and orderly shutdown
     * of scheduled tasks.
     */
    @PreDestroy
    public void shutdown() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
        }
    }
}

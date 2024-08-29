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

@Service
@Slf4j
public class OpenapiGeoService implements IOpenapiGeoService{
    @Autowired
    private OpenapiGeoRepository openapiGeoRepository;
    @Autowired
    private OpenapiReportProperties reportProperties;
    private final List<OpenapiGeoEntity> entities = new LinkedList<>();
    private long lastUpdatedTime = System.currentTimeMillis();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void report(OpenapiGeoEntity entity) {
        write(entity);
    }

    @PostConstruct
    public void init() {
        OpenapiReportProperties.GeoReportProperties geoReportProperties = reportProperties.getGeo();
        ThreadFactory factory = Thread.ofVirtual().name("geo-report-virtual-thread").factory();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(factory);
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (true) {
                int size = entities.size();
                if (size > geoReportProperties.getReportSize() ||
                        System.currentTimeMillis() - lastUpdatedTime > geoReportProperties.getReportTimeout()) {
                    List<OpenapiGeoEntity> newEntities = readAndClear();
                    lastUpdatedTime = System.currentTimeMillis();
                    openapiGeoRepository.saveAll(newEntities);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(geoReportProperties.getReportWithoutDataTimeout());
                } catch (InterruptedException ignored) {}
            }
        }, geoReportProperties.getScheduledDelay(), geoReportProperties.getScheduledPeriod(), TimeUnit.MILLISECONDS);
    }

    private List<OpenapiGeoEntity> readAndClear() {
        readWriteLock.writeLock();
        List<OpenapiGeoEntity> newEntities;
        try {
            newEntities= new ArrayList<>(entities);
            entities.clear();
        }finally {
            readWriteLock.writeLock().unlock();;
        }
        return newEntities;
    }

    private void write(OpenapiGeoEntity entity) {
        readWriteLock.readLock();
        try {
            entities.addLast(entity);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @PreDestroy
    public void shutdown() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
        }
    }
}

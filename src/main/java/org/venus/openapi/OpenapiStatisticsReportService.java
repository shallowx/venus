package org.venus.openapi;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Slf4j
public class OpenapiStatisticsReportService implements IOpenapiStatisticsReportService {
    @Autowired
    private OpenapiStatisticsRepository openapiStatisticsRepository;
    @Autowired
    private OpenapiReportProperties reportProperties;
    private final List<OpenapiStatisticsEntity> entities = new LinkedList<>();
    private long lastUpdatedTime = System.currentTimeMillis();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void report(OpenapiStatisticsEntity entity) {
        this.write(entity);
    }

    @PostConstruct
    public void init() {
        OpenapiReportProperties.StatisticsReportProperties statisticsReportProperties = reportProperties.getStatistics();
        ThreadFactory factory = Thread.ofVirtual().name("statistics-report-virtual-thread").factory();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(factory);
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (true) {
                int size = entities.size();
                if (size > statisticsReportProperties.getReportSize() ||
                        System.currentTimeMillis() - lastUpdatedTime > statisticsReportProperties.getReportTimeout()) {
                    List<OpenapiStatisticsEntity> newEntities = readAndClear();
                    lastUpdatedTime = System.currentTimeMillis();
                    openapiStatisticsRepository.saveAll(newEntities);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(statisticsReportProperties.getReportWithoutDataTimeout());
                } catch (InterruptedException ignored) {}
            }
        }, statisticsReportProperties.getScheduledDelay(), statisticsReportProperties.getScheduledPeriod(), TimeUnit.MILLISECONDS);
    }

    private List<OpenapiStatisticsEntity> readAndClear() {
        readWriteLock.writeLock();
        List<OpenapiStatisticsEntity> newEntities;
        try {
            newEntities= new ArrayList<>(entities);
            entities.clear();
        }finally {
            readWriteLock.writeLock().unlock();;
        }
        return newEntities;
    }

    private void write(OpenapiStatisticsEntity entity) {
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

package org.venus.metrics;

public interface CStats {
    StatsCounter statsCounter = new ConcurrentStatsCounter();
    default Stats stats() {
        return statsCounter.snapshot();
    }
}

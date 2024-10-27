package org.venus.metrics;

public interface TimeCStats extends CStats {
    TimeStatsCounter counter = new TimeConcurrentStatsCounter();
    default Stats timeStats() {
        return counter.snapshot();
    }
}

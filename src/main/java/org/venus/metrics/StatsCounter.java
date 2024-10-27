package org.venus.metrics;

public interface StatsCounter {
    void recordSuccess(int count);
    void recordFailure(int count, int code);
    Stats snapshot();
}

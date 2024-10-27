package org.venus.metrics;

public interface TimeStatsCounter extends StatsCounter {
    void totalTime(long time);
}

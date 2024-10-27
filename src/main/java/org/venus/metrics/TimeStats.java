package org.venus.metrics;

import java.util.Objects;

public class TimeStats extends Stats {

    private final long totalTime;

    public TimeStats(long successCount, long failureCount, long error4xxCount, long error5xxCount, long totalTime) {
        super(successCount, failureCount, error4xxCount, error5xxCount);
        this.totalTime = totalTime;
    }

    public long totalTime() {
        return totalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TimeStats timeStats = (TimeStats) o;
        return totalTime == timeStats.totalTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), totalTime);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' +
                "totalTime=" + totalTime +
                '}';
    }
}

package org.venus.metrics;

import java.util.Objects;

public class Stats {

    private static final Stats EMPTY = new Stats(0L,0L,0L,0L);

    private final long successCount;
    private final long failureCount;
    private final long error4xxCount;
    private final long error5xxCount;

    public Stats(long successCount, long failureCount, long error4xxCount, long error5xxCount) {
        if (successCount < 0 || failureCount < 0 || error4xxCount < 0 || error5xxCount < 0) {
            throw new IllegalArgumentException();
        }
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.error4xxCount = error4xxCount;
        this.error5xxCount = error5xxCount;
    }

    public static Stats empty() {
        return EMPTY;
    }
    public long successCount() {
        return successCount;
    }
    public long failureCount() {
        return failureCount;
    }

    public long requestCount() {
        return saturateAdd(successCount, failureCount);
    }

    public double successRate() {
        long requestCount = requestCount();
        return requestCount == 0 ? 1.0D : (double) successCount / requestCount;
    }

    private long saturateAdd(long a, long b) {
        long nativeNum = a + b;
        return (a ^ b) < 0L | (a ^ nativeNum) >= 0L ? nativeNum : Long.MIN_VALUE + (nativeNum >>> 63 ^ 1L);
    }

    public long error4xxCount() {
        return error4xxCount;
    }
    public long error5xxCount() {
        return error5xxCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stats stats = (Stats) o;
        return successCount == stats.successCount && failureCount == stats.failureCount && error4xxCount == stats.error4xxCount && error5xxCount == stats.error5xxCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(successCount, failureCount, error4xxCount, error5xxCount);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' +
                "successCount=" + successCount +
                ", failureCount=" + failureCount +
                ", error4xxCount=" + error4xxCount +
                ", error5xxCount=" + error5xxCount +
                '}';
    }
}

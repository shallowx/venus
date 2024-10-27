package org.venus.metrics;

import java.util.concurrent.atomic.LongAdder;

public class ConcurrentStatsCounter implements StatsCounter {

    protected final LongAdder successCount;
    protected final LongAdder failureCount;
    protected final LongAdder error4xxCount;
    protected final LongAdder error5xxCount;

    public ConcurrentStatsCounter() {
        this.successCount = new LongAdder();
        this.failureCount = new LongAdder();
        this.error4xxCount = new LongAdder();
        this.error5xxCount = new LongAdder();
    }

    @Override
    public void recordSuccess(int count) {
        successCount.add(count);
    }

    @Override
    public void recordFailure(int count, int code) {
        failureCount.add(count);
        if (code >= 400 && code < 500) {
            error4xxCount.add(count);
        }

        if (code >= 500 && code < 600) {
            error5xxCount.add(count);
        }
    }

    @Override
    public Stats snapshot() {
        return new Stats(negative2MaxValue(successCount.sum()), negative2MaxValue(failureCount.sum()), negative2MaxValue(error4xxCount.sum()),negative2MaxValue(error5xxCount.sum()));
    }

    protected long negative2MaxValue(long count) {
        return count >= 0 ? count : Long.MAX_VALUE;
    }
}

package org.venus.metrics;

import java.util.concurrent.atomic.LongAdder;

public class TimeConcurrentStatsCounter extends ConcurrentStatsCounter implements TimeStatsCounter {

    private final LongAdder totalTime;

    public TimeConcurrentStatsCounter() {
        super();
        this.totalTime = new LongAdder();
    }

    @Override
    public void totalTime(long time) {
        totalTime.add(time);
    }

    @Override
    public Stats snapshot() {
        return new TimeStats(negative2MaxValue(super.successCount.sum()), negative2MaxValue(super.failureCount.sum()), negative2MaxValue(super.error4xxCount.sum()), negative2MaxValue(super.error4xxCount.sum()), negative2MaxValue(totalTime.sum()));
    }
}

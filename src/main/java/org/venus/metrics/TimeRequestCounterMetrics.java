package org.venus.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class TimeRequestCounterMetrics<C extends TimeCStats> extends RequestCounterMetrics<C> {
    public TimeRequestCounterMetrics(C c, String name, WeakReference<C> reference, Iterable<Tag> tags) {
        super(c, name, reference, tags);
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
        C c = getC();
        TimeGauge.builder("http_request_total_time_counter", c, TimeUnit.MILLISECONDS, d -> ((TimeStats)d.timeStats()).totalTime()).tags(getTags()).register(registry);
    }
}

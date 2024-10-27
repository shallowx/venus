package org.venus.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.lang.ref.WeakReference;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class RequestCounterMetrics<C extends CStats> extends RequestCounterMeterBinder<C> {

    public RequestCounterMetrics(C c, String name, WeakReference<C> reference, Iterable<Tag> tags) {
        super(c, name, reference, tags);
    }

    @Override
    protected Long successCount() {
        return get(c -> c.stats().successCount(),null);
    }

    @Override
    protected Long failureCount() {
        return get(c -> c.stats().failureCount(),null);
    }

    @Override
    protected Long error4xxCount() {
        return get(c -> c.stats().error4xxCount(),null);
    }

    @Override
    protected Long error5xxCount() {
        return get(c -> c.stats().error5xxCount(),null);
    }

    @Override
    protected Double successRate() {
        return get2Double(c -> c.stats().successRate(),null);
    }

    protected Long get(ToLongFunction<C> function, Long defaultValue) {
        C c = getC();
        return c != null ? Long.valueOf(function.applyAsLong(c)) : defaultValue;
    }

    protected Double get2Double(ToDoubleFunction<C> function, Long defaultValue) {
        C c = getC();
        return c != null ? Double.valueOf(function.applyAsDouble(c)) : defaultValue;
    }


    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {

    }
}

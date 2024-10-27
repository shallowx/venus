package org.venus.metrics;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.lang.NonNull;

import java.lang.ref.WeakReference;

public abstract class RequestCounterMeterBinder<C> implements MeterBinder {

    private final WeakReference<C> reference;
    private final Iterable<Tag> tags;

    public RequestCounterMeterBinder(C c, String name, WeakReference<C> reference, Iterable<Tag> tags) {
        this.reference = new WeakReference<>(c);
        this.tags = Tags.concat(tags, "target", name);
    }

    @Override
    public void bindTo(@NonNull MeterRegistry meterRegistry) {
        C c = reference.get();
        FunctionCounter.builder("", c, c1 -> {
            Long failure = failureCount();
            return failure == null ? 0L : failure;
        }).tags(tags).tag("http_request_status","failure").register(meterRegistry);

        FunctionCounter.builder("", c, c1 -> {
            Long success = successCount();
            return success == null ? 0L : success;
        }).tags(tags).tag("http_request_status","success").register(meterRegistry);

        FunctionCounter.builder("", c, c1 -> {
            Long error4xx = error4xxCount();
            return error4xx == null ? 0L : error4xx;
        }).tags(tags).tag("http_request_status","error_4xx").register(meterRegistry);

        FunctionCounter.builder("", c, c1 -> {
            Long error5xx = error5xxCount();
            return error5xx == null ? 0L : error5xx;
        }).tags(tags).tag("http_request_status","error_5xx").register(meterRegistry);

        FunctionCounter.builder("", c, c1 -> {
            Double successRate = successRate();
            return successRate == null ? 0.0D : successRate;
        }).tags(tags).tag("http_request_status","success_rate").register(meterRegistry);
    }

    protected C getC() {
        return reference.get();
    }

    protected Iterable<Tag> getTags() {
        return tags;
    }

    protected abstract Long successCount();
    protected abstract Long failureCount();
    protected abstract Long error4xxCount();
    protected abstract Long error5xxCount();
    protected abstract Double successRate();

    protected abstract void bindImplementationSpecificMetrics(MeterRegistry registry);
}

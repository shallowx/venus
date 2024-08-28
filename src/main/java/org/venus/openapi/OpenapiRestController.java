package org.venus.openapi;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.venus.support.GenericListRestApiResponse;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusRestApiCode;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/openapi")
@Slf4j
public class OpenapiRestController {

    @Autowired
    private IOpenapiService iOpenapiService;
    private static final String REDIRECT_302 = "302";
    private static final String REDIRECT_301 = "301";

    private static final MeterRegistry registry = Metrics.globalRegistry;
    private static final ConcurrentHashMap<String, Counter> redirects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> requestCounter = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * If redirection through this system is not required, this api is suitable. And redirected by the caller
     * However, if the caller does not report the analysis data, the data may be lost.
     *
     * If the caller needs to cache the response, and it needs to register a callback method to update the new data from venus.
     * Register method see 'admin(package)' function
     */
    @GetMapping("/mapping")
    public GenericRestApiResponse<OpenapiResponse> get(@RequestParam String original) {
        try {
            return GenericRestApiResponse.success(OpenapiResponse.from(
                    iOpenapiService.get(original)
            ));
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Get venus openapi original-url and url-encode mapping entity failure", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    /**
     * If redirection through this system is not required, this api is suitable. And redirected by the caller
     * However, if the caller does not report the analysis data, the data may be lost.
     *
     * If the caller needs to cache the response, and it needs to register a callback method to update the new data from venus.
     * Register method see 'admin(package)' function
     */
    @GetMapping("/lists")
    public GenericListRestApiResponse<OpenapiResponse> lists() {
        try {
            return GenericListRestApiResponse.success(OpenapiResponse.from(
                    iOpenapiService.lists()
            ));
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Get venus openapi original-url and url-encode mapping entity failure", e);
            }
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    /**
     * If redirection through this system is required and need to record the redirect result
     *
     */
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirect(@RequestParam @NotEmpty @Validated String encode) {
        OpenapiEntity entity = iOpenapiService.redirect(encode);
        if (entity == null) {
            submit(encode, "http_redirect_unknown_url", "unknown");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        int redirect = entity.getRedirect();
        if (String.valueOf(redirect).equals(REDIRECT_301)) {
            submit(encode, "http_redirect_301", entity.getOriginalUrl());
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .location(URI.create(entity.getOriginalUrl()))
                    .build();
        }

        if (String.valueOf(redirect).equals(REDIRECT_302)) {
            submit(encode, "http_redirect_302", entity.getOriginalUrl());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(entity.getOriginalUrl()))
                    .build();
        }

        submit(encode, "http_redirect_unknown_status", entity.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    private void submit(String encode, String name, String url) {
        executor.submit(() -> {
            getRedirectCounterAndIncrement(encode, name, url);
            getHttpRequestCounterAndIncrement("default");
        });
    }

    public void getHttpRequestCounterAndIncrement(String key) {
        Counter counter = requestCounter.get(key);
        if (counter == null) {
            counter = requestCounter.computeIfAbsent(key,
                    s -> Counter.builder("http_redirect_request_total_count")
                            .tags(Tags.of("application", "venus")
                                    .and(Tags.of("version", "1.0.0"))
                            ).register(registry));
        }
        counter.increment();
    }

    private void getRedirectCounterAndIncrement(String key, String name, String original) {
        Counter counter = redirects.get(key);
        if (counter == null) {
            counter = redirects.computeIfAbsent(key,
                    s -> Counter.builder(name)
                            .tags(Tags.of("encode", key)
                                    .and(Tags.of("application", "venus"))
                                    .and(Tags.of("version", "1.0.0"))
                                    .and(Tags.of("original", original))
                            ).register(registry));
        }
        counter.increment();
    }
}

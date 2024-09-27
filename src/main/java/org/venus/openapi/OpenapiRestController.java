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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.venus.metrics.MetricsConstants;
import org.venus.support.GenericListRestApiResponse;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusRestApiCode;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OpenapiRestController is a REST controller that handles API requests related to
 * OpenAPI redirections and mappings. It provides endpoints for retrieving and
 * redirecting URLs while also tracking request and redirect metrics.
 */
@RestController
@RequestMapping("/openapi/v1")
@Slf4j
public class OpenapiRestController {

    /**
     * This variable represents an autowired instance of IOpenapiService.
     * The IOpenapiService interface is expected to provide methods to
     * interact with an OpenAPI service, which typically offers endpoints
     * to access various functionalities.
     *
     * The autowiring mechanism ensures that an appropriate implementation
     * of this service is injected at runtime, facilitating dependency
     * management and promoting loose coupling within the application.
     */
    private final IOpenapiService iOpenapiService;
    private final String errorUri;

    /**
     * Constructor for OpenapiRestController.
     *
     * @param iOpenapiService the OpenAPI service instance used for handling business logic
     * @param properties      the properties configuration object containing default settings
     */
    @Autowired
    public OpenapiRestController(IOpenapiService iOpenapiService, OpenapiInitializerProperties properties) {
        this.iOpenapiService = iOpenapiService;
        this.errorUri = properties.getDefaultRedirectUrl();
    }

    /**
     * A constant representing the HTTP status code for a temporary redirect.
     * The value "302" indicates that the requested resource resides temporarily
     * under a different URI and the user agent should perform a temporary redirect.
     */
    private static final short REDIRECT_302 = 302;
    /**
     * A constant representing the HTTP status code for "Moved Permanently".
     * This status code indicates that the requested resource has been
     * permanently moved to a new URL.
     */
    private static final short REDIRECT_301 = 301;

    /**
     * A static and final instance of {@code MeterRegistry} that is initialized with
     * the global registry from the Metrics library. This registry is used to collect
     * and manage various metrics across the application.
     */
    private static final MeterRegistry registry = Metrics.globalRegistry;
    /**
     * A thread-safe map that holds URL redirects.
     *
     * This variable uses a {@link ConcurrentHashMap} where the key represents the
     * original URL string, and the value is a {@link Counter} object keeping track
     * of the number of times the redirect has occurred. The use of
     * {@link ConcurrentHashMap} ensures that the map can be safely accessed and
     * modified by multiple threads concurrently.
     */
    private static final ConcurrentHashMap<String, Counter> redirects = new ConcurrentHashMap<>();
    /**
     * A thread-safe map that keeps track of request counts for different keys.
     * The keys are of type String, representing a specific request identifier.
     * The values are Counter objects, which hold the count associated with each request.
     * Utilizes ConcurrentHashMap to ensure thread safety when multiple threads
     * update the request counts concurrently.
     */
    private final ConcurrentHashMap<String, Counter> requestCounter = new ConcurrentHashMap<>();
    /**
     * A static final instance of ExecutorService that uses a virtual thread-per-task executor.
     * This executor service is designed to efficiently handle a large number of concurrently executing tasks.
     * Virtual threads are lightweight and managed by the Java runtime, making them suitable for high-concurrency applications.
     */
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Handles a GET request to fetch an OpenAPI mapping entity based on the provided original URL.
     *
     * @param original the original URL to be encoded and mapped.
     * @return a GenericRestApiResponse containing the OpenapiResponse if the mapping is successful,
     *         or an error response if any exception occurs.
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
            return GenericRestApiResponse.fail(VenusRestApiCode.OPENAPI_EXCEPTION, VenusRestApiCode.OPENAPI_EXCEPTION.message());
        }
    }

    /**
     * Handles GET requests to retrieve a list of OpenAPI responses.
     *
     * @return {@code GenericListRestApiResponse<OpenapiResponse>} containing the list of OpenAPI responses if successful,
     * or an error response otherwise.
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
            return GenericListRestApiResponse.fail(VenusRestApiCode.OPENAPI_EXCEPTION, VenusRestApiCode.OPENAPI_EXCEPTION.message());
        }
    }

    /**
     * Redirects the request based on the encoded parameter provided.
     *
     * @param encode the encoded string used to determine the redirect URL; should not be empty and must be valid.
     * @return a ResponseEntity with an appropriate status and location headers based on the redirect information retrieved.
     */
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirect(@RequestParam @NotEmpty @Validated String encode) {
         try {
             OpenapiEntity entity = iOpenapiService.redirect(encode);
             if (entity == null) {
                 submit(encode, "http_redirect_unknown_url", "unknown");
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
             }

             int redirect = entity.getRedirect();
             if (redirect == REDIRECT_301) {
                 submit(encode, "http_redirect_301", entity.getOriginalUrl());
                 return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                         .location(URI.create(entity.getOriginalUrl()))
                         .build();
             }

             if (redirect == REDIRECT_302) {
                 submit(encode, "http_redirect_302", entity.getOriginalUrl());
                 return ResponseEntity.status(HttpStatus.FOUND)
                         .location(URI.create(entity.getOriginalUrl()))
                         .build();
             }

             submit(encode, "http_redirect_unknown_status", entity.getOriginalUrl());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
         } catch (Exception e) {
             if (log.isErrorEnabled()) {
                 log.error("Get venus openapi redirect failure, and will redirect the default error uri[{}]", errorUri, e);
             }
             submit(encode, "http_redirect_default_error_uri", errorUri);
             return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).location(URI.create(errorUri)).build();
         }
    }

    /**
     * Handles error requests and returns an error message view.
     *
     * @return a string indicating the view name for error handling
     */
    @GetMapping("/error")
    public String error() {
        return "error/503: please check the logs for more information";
    }

    /**
     * Submits a task to the executor that performs HTTP request and redirect counting.
     *
     * @param encode The encoding to be used.
     * @param name The name associated with the task.
     * @param url The URL to be accessed.
     */
    private void submit(String encode, String name, String url) {
        executor.submit(() -> {
            getRedirectCounterAndIncrement(encode, name, url);
            getHttpRequestCounterAndIncrement("default");
        });
    }

    /**
     * Retrieves the HTTP request counter associated with the specified key and increments it.
     * If the counter does not exist, it initializes a new counter and registers it.
     *
     * @param key the unique identifier for the HTTP request counter
     */
    public void getHttpRequestCounterAndIncrement(String key) {
        Counter counter = requestCounter.get(key);
        if (counter == null) {
            counter = requestCounter.computeIfAbsent(key,
                    s -> Counter.builder("http_redirect_request_total_count")
                            .tags(Tags.of(MetricsConstants.TYPE_APPLICATION_NAME, MetricsConstants.DEFAULT_APPLICATION_NAME)
                                    .and(Tags.of(MetricsConstants.TYPE_VERSION, MetricsConstants.DEFAULT_APPLICATION_VERSION))
                            ).register(registry));
        }
        counter.increment();
    }

    /**
     * This method retrieves a redirect counter associated with a given key from a map. If the counter does not exist,
     * it creates a new counter using the given key, name, and original value, associates it with the key,
     * and then increments the counter.
     *
     * @param key The key used to retrieve and associate the counter from the map.
     * @param name The name used for creating a new counter if it does not already exist.
     * @param original The original value to be tagged along with the counter if it needs to be created.
     */
    private void getRedirectCounterAndIncrement(String key, String name, String original) {
        Counter counter = redirects.get(key);
        if (counter == null) {
            counter = redirects.computeIfAbsent(key,
                    s -> Counter.builder(name)
                            .tags(Tags.of("encode", key)
                                    .and(Tags.of(MetricsConstants.TYPE_APPLICATION_NAME, MetricsConstants.DEFAULT_APPLICATION_NAME))
                                    .and(Tags.of(MetricsConstants.TYPE_VERSION, MetricsConstants.DEFAULT_APPLICATION_VERSION))
                                    .and(Tags.of(MetricsConstants.TYPE_ORIGIN, original))
                            ).register(registry));
        }
        counter.increment();
    }
}

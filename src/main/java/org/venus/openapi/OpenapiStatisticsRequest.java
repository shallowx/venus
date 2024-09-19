package org.venus.openapi;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * OpenapiStatisticsRequest is a data transfer object representing the request
 * for reporting OpenAPI statistics. It includes various details such as
 * IP address, user agent, referer, and the time the link was clicked.
 *
 * This class uses Lombok annotations for boilerplate code reduction such as
 * getters, setters, constructors, and builder pattern methods.
 *
 * Fields in this class are validated to ensure that none of the necessary information
 * is left out of the request.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenapiStatisticsRequest {
    /**
     * The unique identifier for the OpenAPI statistics request.
     */
    private long id;
    /**
     * Unique identifier for the link associated with this OpenAPI statistics request.
     */
    private long linkId;
    /**
     * The IP address from which the OpenAPI request was made.
     * This field is mandatory and cannot be null or empty.
     */
    @NotNull(message = "The openapi statistics ip cannot be empty")
    private String ip;
    /**
     * Represents the user agent of the client making the request.
     * This field is mandatory and cannot be null.
     */
    @NotNull(message = "The openapi statistics userAgent cannot be empty")
    private String userAgent;
    /**
     * Represents the HTTP referer in the OpenAPI statistics request.
     * This field captures the URL of the page that referred to the current request.
     *
     * The referer field is mandatory and must not be empty.
     */
    @NotNull(message = "The openapi statistics referer cannot be empty")
    private String referer;
    /**
     * Represents the exact date and time when the link was clicked.
     *
     * This field is mandatory and must not be null.
     * It is used to track the timestamp of user interaction with the link.
     */
    @NotNull(message = "The openapi statistics clickedAt cannot be empty")
    private LocalDateTime clickedAt;
}

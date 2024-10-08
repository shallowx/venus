package org.venus.openapi;

import lombok.*;
import org.venus.cache.ValueWrapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a response object compliant with the OpenAPI specification.
 *
 * This class is designed to hold the response data for OpenAPI endpoints, including the response code,
 * the HTTP redirect status, the original URL, expiration details, and activation status.
 */
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OpenapiResponse {
    /**
     * Represents the unique identifier code for the URL mapping in the OpenAPI response.
     * This code is either provided by the user or generated by the system to uniquely identify a specific mapping.
     */
    private String code;
    /**
     * Represents the HTTP redirect status code.
     *
     * This value is intended to specify the type of HTTP redirect to be performed, usually
     * set to either 301 for permanent redirects or 302 for temporary redirects.
     */
    private int redirect;
    /**
     * Represents the original URL associated with the response.
     *
     * This variable stores the full destination link that is to be managed or shortened by the OpenapiResponse.
     * It is a crucial element in redirection operations, holding the complete URL to which a shortened link will redirect.
     */
    private String originalUrl;
    /**
     * The expiration timestamp of the response.
     *
     * This variable indicates when the response will expire.
     * It is used to manage the lifecycle of the response and ensure it is no longer considered valid after this timestamp.
     */
    private LocalDateTime expiresAt;
    /**
     * Indicates whether the response is currently active.
     *
     * This variable helps determine the operational status of the response.
     * If true, the response is considered active; otherwise, it is inactive.
     */
    private boolean isActive;

    /**
     * Converts an OpenapiEntity object to an OpenapiResponse object.
     *
     * @param entity The OpenapiEntity object to convert.
     * @return An OpenapiResponse object containing the data from the provided entity.
     */
    public static OpenapiResponse from(ValueWrapper entity) {
        return OpenapiResponse.builder()
                .code(entity.getCode())
                .originalUrl(entity.getOriginalUrl())
                .redirect(entity.getRedirect())
                .expiresAt(entity.getExpiresAt())
                .isActive(entity.getIsActive() != 0)
                .build();
    }

    /**
     * Converts a list of OpenapiEntity objects to a list of OpenapiResponse objects.
     *
     * This method takes a list of OpenapiEntity instances and transforms each
     * instance to an OpenapiResponse using the OpenapiResponse::from method.
     * If the input list is null or empty, it returns an empty list.
     *
     * @param entities List of OpenapiEntity objects to be converted.
     * @return A list of OpenapiResponse objects.
     */
    public static List<OpenapiResponse> from(List<ValueWrapper> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(OpenapiResponse::from)
                .collect(Collectors.toList());
    }
}

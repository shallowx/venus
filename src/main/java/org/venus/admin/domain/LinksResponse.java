package org.venus.admin.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a response object for link data.
 * It is used for transferring link details from the backend to the frontend.
 *
 * The class includes the following attributes:
 * - id: The unique identifier for the link.
 * - code: The unique code associated with the link.
 * - redirect: The redirect status of the link.
 * - originalUrl: The original URL which the link redirects to.
 * - createdAt: The timestamp indicating when the link was created.
 * - expiresAt: The timestamp indicating when the link expires.
 * - isActive: A boolean indicating whether the link is active.
 *
 * The class provides builder methods for constructing instances and static methods
 * for converting from a LinksEntity object or a list of LinksEntity objects.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LinksResponse {
    /**
     * The unique identifier for the link.
     */
    private long id;
    /**
     * The unique code associated with the link.
     * It is used for identifying and accessing the specific link.
     */
    private String code;
    /**
     * The redirect status of the link.
     * This integer field typically represents a status code indicating the type of redirection.
     * For example, it may denote HTTP status codes like 301 for permanent redirect or 302 for temporary redirect.
     */
    private int redirect;
    /**
     * The original URL which the link redirects to.
     * This field must be unique and can have a maximum length of 500 characters.
     */
    private String originalUrl;
    /**
     * The timestamp indicating when the link was created.
     */
    private LocalDateTime createdAt;
    /**
     * The timestamp indicating when the link expires.
     * This field is used to determine the expiration date and time of the link.
     */
    private LocalDateTime expiresAt;
    /**
     * A boolean indicating whether the link is active.
     * This flag determines if the associated link should be considered active.
     */
    private boolean isActive;

    /**
     * Converts a LinksEntity object to a LinksResponse object.
     *
     * @param links The LinksEntity object to be converted.
     * @return A new LinksResponse object with values copied from the provided LinksEntity.
     */
    public static LinksResponse from(LinksEntity links) {
        return LinksResponse.builder()
                .id(links.getId())
                .code(links.getCode())
                .redirect(links.getRedirect())
                .originalUrl(links.getOriginalUrl())
                .createdAt(links.getCreatedAt())
                .expiresAt(links.getExpiresAt())
                .isActive(links.getIsActive() != 0)
                .build();
    }

    /**
     * Converts a list of LinksEntity objects into a list of LinksResponse objects.
     *
     * @param links the list of LinksEntity objects to be converted.
     * @return a list of LinksResponse objects; an empty list if the input is null or empty.
     */
    public static List<LinksResponse> from(List<LinksEntity> links) {
        if (links == null || links.isEmpty()) {
            return Collections.emptyList();
        }
        return links.stream()
                .map(LinksResponse::from)
                .collect(Collectors.toList());
    }
}

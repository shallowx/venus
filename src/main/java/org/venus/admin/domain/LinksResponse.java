package org.venus.admin.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LinksResponse is a Data Transfer Object (DTO) representing the response format for link-related operations.
 * This class encapsulates attributes of a link such as its ID, code, redirect status, original URL,
 * creation timestamp, expiration timestamp, and active status.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LinksResponse {
    /**
     * Unique identifier for the link.
     */
    private long id;
    /**
     * The unique code associated with the link.
     * This value is used for identifying and accessing the specific link.
     */
    private String code;
    /**
     * The redirect status of the link.
     * This field indicates how the link should be redirected.
     */
    private int redirect;
    /**
     * The original URL associated with the link.
     */
    private String originalUrl;
    /**
     * The timestamp indicating when the link was created.
     */
    private LocalDateTime createdAt;
    /**
     * The date and time when the link expires.
     * This value is used to determine the validity period of the link.
     */
    private LocalDateTime expiresAt;
    /**
     * Indicates whether the link is currently active.
     * A value of true means the link is active, while false means it is inactive.
     */
    private boolean isActive;

    /**
     * Converts a given LinksEntity object to a LinksResponse object.
     *
     * @param links the LinksEntity object to be converted
     * @return a LinksResponse object representing the given LinksEntity
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
     * Converts a list of LinksEntity objects to a list of LinksResponse objects.
     *
     * @param links the list of LinksEntity objects to be converted
     * @return a list of LinksResponse objects representing the given LinksEntity objects
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

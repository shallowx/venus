package org.venus.cache;

import lombok.*;

import java.time.LocalDateTime;

/**
 * ValueWrapper represents the essential data for URL mapping and redirection.
 *
 * This class holds the attributes needed to map a short code to the original URL
 * and manage redirection behavior, including tracking the expiry and activation status.
 *
 * It is typically used within URL shortening services or systems handling URL redirection
 * and mapping.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ValueWrapper {
    /**
     * Unique identifier for the ValueWrapper instance.
     *
     * This ID serves as a primary key used to uniquely distinguish each instance
     * of ValueWrapper in storage and other operations.
     */
    private long id;
    /**
     * Represents a unique short code associated with a URL.
     *
     * This field holds the shortened code that maps to the original URL
     * for redirection purposes in a URL shortening service.
     */
    private String code;
    /**
     * Represents the number of times a URL redirection has occurred.
     *
     * This variable is used within the ValueWrapper class to track how many times
     * the associated short code has redirected users to the original URL.
     */
    private int redirect;
    /**
     * The original URL that corresponds to a shortened URL code.
     *
     * This attribute holds the complete URL before it was shortened. It is used
     * to redirect the user to the intended web page when the shortened URL code
     * is accessed.
     */
    private String originalUrl;
    /**
     * The date and time when this URL mapping expires.
     *
     * Indicates when the current URL mapping should be considered inactive and not valid for redirection.
     * This is typically used to manage the lifecycle of shortened URLs, ensuring they are only valid
     * for a certain period.
     */
    private LocalDateTime expiresAt;
    /**
     * Indicates whether the URL mapping is currently active.
     *
     * A value of 1 denotes that the URL mapping is active and can be used for redirection,
     * while a value of 0 indicates that the URL mapping has been deactivated or is not in use.
     */
    private short isActive;
}

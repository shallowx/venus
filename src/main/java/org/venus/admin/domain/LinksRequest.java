package org.venus.admin.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.venus.admin.annotations.FutureDate;

/**
 * Represents a request object for managing links with attributes for link ID, code, redirect HTTP status,
 * original URL, expiration date, and active status.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LinksRequest {

    /**
     * need to ensure {@code id} its unique
     */
    @NotNull(message = "The links mapping id cannot be empty")
    @Min(value = 0, message = "The links mapping id min-value is 0")
    @Max(value = Long.MAX_VALUE, message = "The links mapping id max-value is 0x7fffffffffffffffL")
    private long id;

    /**
     * if {@code code} is not null, need to ensure its unique and its length must be 8, and it needs to conform to 62-bit encoded characters, eg:[0-9] [a-z] [A-Z]
     * if its null, it will create by venus system.
     */
    @Length(min = 0, max = 8, message = "The links code character length must be 8")
    private String code;

    /**
     * The HTTP status code for redirection of the link.
     * It must be either 301 (Moved Permanently) or 302 (Found).
     * This field cannot be null and must lie between 301 and 302 inclusive.
     */
    @NotNull(message = "The links redirect code cannot be empty")
    @Min(value = 301, message = "The min redirect must be 301")
    @Max(value = 302, message = "The max redirect must be 302")
    private int redirect;

    /**
     * Represents the original URL of a link. This URL must not be empty and its length must be within the range [0, 500].
     * Used as an attribute within a request object for managing link mappings.
     *
     * Constraints:
     * - Must not be empty.
     * - Length must be between 0 and 500 characters.
     */
    @NotEmpty(message = "The links original URL cannot be empty")
    @Length(min = 0, max = 500, message = "The links original URL length in [0,500]")
    private String originalUrl;

    /**
     * Represents the expiration date and time for the link.
     *
     * The value of this field must be a valid date-time string in the format "yyyy-MM-dd HH:mm:ss" and must be in the future.
     * This ensures that links have a valid future expiration date.
     *
     * Validations:
     * - Must not be null.
     * - Must be a future date.
     */
    @NotNull(message = "The links expires_at cannot be empty")
    @FutureDate
    private String expiresAt;

    /**
     * Indicates whether the link is active.
     *
     * The value must be either 0 or 1:
     * - 0: Inactive
     * - 1: Active
     *
     * This field is not nullable and must adhere to the specified
     * minimum and maximum constraints.
     *
     * - Minimum value: 0
     * - Maximum value: 1
     *
     * It serves to manage the active status of a link within the system.
     */
    @NotNull
    @Min(value = 0, message = "The links is_active min-value is 0")
    @Max(value = 1, message = "The links is_active max-value is 1")
    private short isActive;
}

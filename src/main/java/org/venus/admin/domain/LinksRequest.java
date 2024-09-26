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
 * This class represents a request model for managing links.
 * It includes various fields that are validated to ensure they meet specific constraints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LinksRequest {

    /**
     * The identifier for a links mapping.
     *
     * It must satisfy the following conditions:
     * - Not null: the identifier cannot be empty.
     * - Minimum value: 0.
     * - Maximum value: 0x7fffffffffffffffL.
     */
    @NotNull(message = "The links mapping id cannot be empty")
    @Min(value = 0, message = "The links mapping id min-value is 0")
    @Max(value = Long.MAX_VALUE, message = "The links mapping id max-value is 0x7fffffffffffffffL")
    private long id;

    /**
     * Represents the unique code assigned to a link.
     *
     * This code is subject to the following validation constraints:
     * - Minimum length: 0 characters
     * - Maximum length: 8 characters
     *
     * If the code does not satisfy these constraints, an appropriate validation message will be displayed.
     */
    @Length(min = 0, max = 8, message = "The links code character length must be 8")
    private String code;

    /**
     * Represents the HTTP status code used for redirection. This variable must be either 301 or 302.
     * A redirection code 301 indicates a permanent redirect, while 302 indicates a temporary redirect.
     * This variable is validated to ensure it is not empty and falls within the allowed range of values.
     *
     * Constraints:
     * - NotNull: The redirection code cannot be null or empty.
     * - Min: The minimum allowable redirection code is 301.
     * - Max: The maximum allowable redirection code is 302.
     */
    @NotNull(message = "The links redirect code cannot be empty")
    @Min(value = 301, message = "The min redirect must be 301")
    @Max(value = 302, message = "The max redirect must be 302")
    private int redirect;

    /**
     * Represents the original URL for a link in the LinksRequest class.
     *
     * This field is validated to ensure it is not empty and its length
     * must be between 0 and 500 characters, inclusive.
     *
     * The validation constraints applied are:
     * - NotEmpty: The original URL cannot be empty.
     * - Length: The length of the original URL must be in the range [0, 500].
     */
    @NotEmpty(message = "The links original URL cannot be empty")
    @Length(min = 0, max = 500, message = "The links original URL length in [0,500]")
    private String originalUrl;

    /**
     * Specifies the expiration date and time for a link.
     *
     * This field must meet the following criteria:
     * - NotNull: The expiration date cannot be empty.
     * - FutureDate: The expiration date must be a future date in the format "yyyy-MM-dd HH:mm:ss".
     */
    @NotNull(message = "The links expires_at cannot be empty")
    @FutureDate
    private String expiresAt;

    /**
     * Indicates the active status of a link in the LinksRequest class.
     *
     * This field is validated to ensure it is not null and its value must be either 0 or 1:
     * - 0: Inactive
     * - 1: Active
     *
     * The validation constraints applied are:
     * - NotNull: The isActive field cannot be null.
     * - Min: The minimum value allowed is 0.
     * - Max: The maximum value allowed is 1.
     */
    @NotNull
    @Min(value = 0, message = "The links is_active min-value is 0")
    @Max(value = 1, message = "The links is_active max-value is 1")
    private short isActive;
}

package org.venus.openapi;

import lombok.Getter;

/**
 * Enumeration representing the status of an OpenAPI redirect.
 *
 * This enum defines three status values:
 * - UNKNOWN: Represented by the code -1, indicating an unknown status.
 * - UN_ACTIVE: Represented by the code 0, indicating that the redirect is not active.
 * - ACTIVE: Represented by the code 1, indicating that the redirect is active.
 *
 * Each enum value is associated with a specific short code.
 *
 * Methods provided:
 * - of(short code): Returns the corresponding OpenapiRedirectStatusEnum value for the given code,
 *   or UNKNOWN if the code does not match any known status.
 */
@Getter
public enum OpenapiRedirectStatusEnum {

    /**
     * Represents an unknown status in the OpenAPI redirect status enumeration.
     * This status is represented by the code -1.
     */
    UNKNOWN((short) -1),
    /**
     * Indicates that the redirect is not active.
     * Represented by the code 0.
     */

    UN_ACTIVE((short) 0), /**
     * Represents the status of an active OpenAPI redirect.
     *
     * This enum value is represented by the code 1, indicating that the redirect is active.
     */
    ACTIVE((short) 1) ;

    /**
     * The short code associated with a specific status of an OpenAPI redirect.
     *
     * This value is used to represent the status and for lookups in the {@link OpenapiRedirectStatusEnum} enumeration.
     */
    public final short code;

    /**
     * Constructs a new OpenapiRedirectStatusEnum with the specified code.
     *
     * @param code the code representing the status of the OpenAPI redirect
     */
    OpenapiRedirectStatusEnum(short code) {
        this.code = code;
    }

    /**
     * Returns the corresponding OpenapiRedirectStatusEnum value for the given code.
     * If the code does not match any known status, this method returns UNKNOWN.
     *
     * @param code The short code representing a specific OpenapiRedirectStatusEnum value.
     * @return The corresponding OpenapiRedirectStatusEnum value, or UNKNOWN if no match is found.
     */
    public static OpenapiRedirectStatusEnum of(short code) {
        for (OpenapiRedirectStatusEnum status : OpenapiRedirectStatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return UNKNOWN;
    }
}

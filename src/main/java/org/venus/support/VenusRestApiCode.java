package org.venus.support;

/**
 * The VenusRestApiCode enum defines standard HTTP-like status codes and messages
 * used by the Venus REST API to indicate success or failure of API requests.
 *
 * Constants:
 * - SUCCESS: Indicates that the request was successful (HTTP code 200).
 * - FAILURE: Indicates an internal server error (HTTP code 500).
 * - BAD_REQUEST: Indicates that the request was invalid (HTTP code 400).
 * - VENUS_ADMIN_EXCEPTION: Indicates an administrative-related exception (custom code 10001).
 * - OPENAPI_EXCEPTION: Indicates an OpenAPI-related exception (custom code 20001).
 *
 * Methods:
 * - code: Returns the integer code associated with the enum constant.
 * - message: Returns the message associated with the enum constant.
 * - of: Converts an integer code to its corresponding VenusRestApiCode enum constant, or null if no match is found.
 */
public enum VenusRestApiCode {
    /**
     * Indicates that the request was successful.
     * Associated with the HTTP status code 200.
     */
    SUCCESS(200, "venus success"),
    /**
     * Represents an internal server error in the Venus REST API.
     * This code is used to indicate that the server encountered an unexpected condition
     * that prevented it from fulfilling the request.
     */
    FAILURE(500, "venus failure"),
    /**
     * Indicates that the request was invalid (HTTP code 400).
     */
    BAD_REQUEST(400, "venus bad request"),
    /**
     * VENUS_ADMIN_EXCEPTION indicates an administrative-related exception within the Venus REST API.
     * This custom status code (10001) helps identify issues specific to administrative operations.
     */
    VENUS_ADMIN_EXCEPTION(10001, "venus admin error"),
    /**
     * Indicates an OpenAPI-related exception, with a custom code 20001.
     */
    OPENAPI_EXCEPTION(20001, "venus openapi error");

    /**
     * Represents the integer code associated with the Venus REST API status.
     */
    private final int code;
    /**
     * The predefined message that describes the status represented by the enum constant.
     */
    private final String message;

    /**
     * Constructs a VenusRestApiCode with the specified code and message.
     *
     * @param code the integer code associated with this instance
     * @param message the message associated with this instance
     */
    VenusRestApiCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Returns the integer code associated with the enum constant.
     *
     * @return the integer code of the enum constant
     */
    public int code() {
        return code;
    }

    /**
     * Returns the message associated with the VenusRestApiCode enum constant.
     *
     * @return the message associated with the enum constant
     */
    public String message() {
        return message;
    }

    /**
     * Converts an integer code to its corresponding VenusRestApiCode enum constant.
     *
     * @param code the integer code to be converted
     * @return the VenusRestApiCode corresponding to the provided code, or null if no match is found
     */
    public static VenusRestApiCode of(int code) {
        for (VenusRestApiCode httpCode : VenusRestApiCode.values()) {
            if (httpCode.code() == code) {
                return httpCode;
            }
        }
        return null;
    }
}

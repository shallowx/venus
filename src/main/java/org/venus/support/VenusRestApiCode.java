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
     * Constant that represents a specific error condition with the VENUS admin.
     * Used for error handling to signify an error code related to admin operations in the VENUS system.
     *
     * Error Code: 10001
     * Description: "venus admin error"
     */
    VENUS_ADMIN_EXCEPTION(10001, "venus admin error"),
    /**
     * VENUS_ADMIN_GEO_EXCEPTION error code and message.
     *
     * This constant represents an error specifically related to geographic
     * issues within the VENUS administrative system.
     *
     * Error Code: 10002
     * Message: "venus geo error"
     */
    VENUS_ADMIN_GEO_EXCEPTION(10002, "venus geo error"),
    /**
     * A constant representing a specific exception case for the Venus admin link.
     * The error code is 10003 and the associated message is "venus link error".
     */
    VENUS_ADMIN_LINK_EXCEPTION(10003, "venus link error"),
    /**
     * The constant VENUS_ADMIN_STATISTICS_EXCEPTION signifies an error specific to Venus statistics functionality.
     * This error is identified by the code 10004 and is described by the message "venus statistics error".
     */
    VENUS_ADMIN_STATISTICS_EXCEPTION(10004, "venus statistics error"),
    /**
     * A constant representing the OpenAPI exception.
     *
     * Error Code: 20001
     *
     * Description: "venus openapi error"
     */
    OPENAPI_EXCEPTION(20001, "venus openapi error"),
    /**
     * This constant represents an error code and message specific to mapping errors
     * encountered in the Venus OpenAPI platform.
     *
     * Error Code: 20002
     * Error Message: "venus openapi mapping error"
     */
    OPENAPI_GEO_EXCEPTION(20002, "venus openapi mapping error"),
    /**
     * Error code and message indicating an issue with the Venus OpenAPI list.
     * The error code is 20003 and the message is "venus openapi list error".
     */
    OPENAPI_STATISTICS_EXCEPTION(20003, "venus openapi list error");

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
    public String message(String error) {
        return String.format("%s: %s", message, error);
    }

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

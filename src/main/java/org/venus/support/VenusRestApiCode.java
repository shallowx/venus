package org.venus.support;

public enum VenusRestApiCode {
    SUCCESS(200, "venus success"),
    FAILURE(500, "venus failure"),
    BAD_REQUEST(400, "venus bad request"),
    VENUS_ADMIN_EXCEPTION(10001, "venus admin exception"),
    OPENAPI_EXCEPTION(20001, "venus openapi exception");

    private final int code;
    private final String message;

    VenusRestApiCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public static VenusRestApiCode of(int code) {
        for (VenusRestApiCode httpCode : VenusRestApiCode.values()) {
            if (httpCode.code() == code) {
                return httpCode;
            }
        }
        return null;
    }
}

package org.venus.admin.support;

public enum VenusRestApiCode {
    SUCCESS(200, "success"),
    FAILURE(500, "failure");

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

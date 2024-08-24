package org.venus.admin.support;

import java.io.Serial;
import java.io.Serializable;

public final class RestApiResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7090019282307269743L;

    private boolean success;
    private String error;
    private T data;
    private int code;
}

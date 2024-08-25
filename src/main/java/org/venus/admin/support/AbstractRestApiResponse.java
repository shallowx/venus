package org.venus.admin.support;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractRestApiResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7090019282307269743L;

    protected boolean success;
    protected String message;
    protected int code;
}

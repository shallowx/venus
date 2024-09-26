package org.venus.support;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * AbstractRestApiResponse provides a common structure for REST API responses.
 * It includes standard fields to indicate the success status, a message, and a response code.
 *
 * @param <T> the type of the response payload
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class AbstractRestApiResponse<T> implements Serializable {
    /**
     * The serialVersionUID is a unique identifier for Serializable classes. This ensures that a
     * previously serialized object can be deserialized even if the class definition has slightly changed.
     */
    @Serial
    private static final long serialVersionUID = -7090019282307269743L;

    /**
     * Indicates whether the API request was successful.
     */
    protected boolean success;
    /**
     * A message providing additional information about the response, typically used to convey
     * errors, warnings, or other pertinent details regarding the success or failure of the request.
     */
    protected String message;
    /**
     * The response code indicating the specific status or outcome of the API call.
     * This is typically an HTTP status code or a custom code defined by the application.
     */
    protected int code;
}

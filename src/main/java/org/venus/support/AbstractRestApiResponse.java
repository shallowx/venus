package org.venus.support;

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
     *
     */
    protected boolean success;
    /**
     * A variable for storing a message in the API response.
     */
    protected String message;
    /**
     * An integer representing the response code of the API request, indicating the specific outcome or status of the request.
     */
    protected int code;
}

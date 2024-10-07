package org.venus.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * GenericRestApiResponse is a generic class that provides a template for standard
 * REST API responses. It extends the AbstractRestApiResponse and includes a
 * payload of type T.
 *
 * @param <T> The type of the response payload.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("rawtypes")
public class GenericRestApiResponse<T> extends AbstractRestApiResponse {
    /**
     * serialVersionUID is a unique identifier used during the deserialization process
     * to ensure that a loaded class corresponds exactly to a serialized object.
     * If no match is found, it will result in an InvalidClassException.
     */
    @Serial
    private static final long serialVersionUID = -3441654766329269405L;

    /**
     * The payload of the REST API response, containing the actual data of type T.
     * It represents the main body content of the API response.
     */
    private T data;

    /**
     * Default protected constructor for GenericRestApiResponse.
     * This constructor invokes the constructor of the superclass AbstractRestApiResponse.
     */
    protected GenericRestApiResponse() {
        super();
    }

    /**
     * Creates a successful {@code GenericRestApiResponse} with the provided data.
     *
     * @param <T> the type of the response payload
     * @param data the payload to be included in the response
     * @return a {@code GenericRestApiResponse} indicating success, containing the provided data
     */
    public static <T> GenericRestApiResponse<T> success(T data) {
        GenericRestApiResponse<T> response = new GenericRestApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = RestApiCode.SUCCESS.message();
        response.code = RestApiCode.SUCCESS.code();
        return response;
    }

    /**
     * Creates a successful GenericRestApiResponse instance with a null payload.
     *
     * @param <T> the type of the response payload
     * @return a successful GenericRestApiResponse instance with a null payload
     */
    public static <T> GenericRestApiResponse<T> success() {
        return success(null);
    }

    /**
     * Creates and returns a GenericRestApiResponse that represents a failed response.
     *
     * @param code the VenusRestApiCode representing the error code
     * @param message a string containing the error message
     * @return a GenericRestApiResponse with success set to false, and populated with the provided error code and message
     */
    public static <T> GenericRestApiResponse<T> fail(RestApiCode code, String message) {
        GenericRestApiResponse<T> response = new GenericRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = code.code();
        return response;
    }

    /**
     * Creates a failed GenericRestApiResponse with the provided code and message.
     *
     * @param <T> the type of the response payload
     * @param code the failure code as a String, which will be parsed into an integer
     * @param message the message describing the failure
     * @return a GenericRestApiResponse indicating failure with the specified code and message
     */
    public static <T> GenericRestApiResponse<T> fail(String code, String message) {
        GenericRestApiResponse<T> response = new GenericRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = Integer.parseInt(code);
        return response;
    }
}

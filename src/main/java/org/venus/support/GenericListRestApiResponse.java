package org.venus.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.List;

/**
 * GenericListRestApiResponse is a generic class used to standardize the response for list-based REST API calls.
 * It extends from AbstractRestApiResponse to inherit common response properties such as success status, message, and code.
 * This class allows the encapsulation of a list of data items of type T in the response.
 *
 * @param <T> the type of elements in the list being encapsulated in the response
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("rawtypes")
public class GenericListRestApiResponse<T> extends AbstractRestApiResponse {
    /**
     * A unique identifier for the serialization runtime associated with the GenericListRestApiResponse class.
     * This ID is used to verify the sender and receiver of a serialized object have loaded classes for that object
     * that are compatible with respect to serialization.
     */
    @Serial
    private static final long serialVersionUID = -6284885253964194599L;

    /**
     * Encapsulates the list of data items to be included in the REST API response.
     * The type of the data items is defined by the generic type parameter T.
     */
    private List<T> data;

    /**
     * Protected constructor for GenericListRestApiResponse.
     * Initializes a new instance of the class by calling the parent class constructor.
     * This constructor is used internally by the class and intended to be extended by subclasses or used in factory methods.
     */
    protected GenericListRestApiResponse() {
        super();
    }

    /**
     * Creates a GenericListRestApiResponse object indicating a successful response.
     *
     * @param <T> the type of elements in the list being encapsulated in the response
     * @param data the list of data items to be included in the response
     * @return a GenericListRestApiResponse object containing the provided data and a success status
     */
    public static <T> GenericListRestApiResponse<T> success(List<T> data) {
        GenericListRestApiResponse<T> response = new GenericListRestApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = RestApiCode.SUCCESS.message();
        response.code = RestApiCode.SUCCESS.code();
        return response;
    }

    /**
     * Creates a successful GenericListRestApiResponse with no data.
     *
     * @param <T> the type of the response data
     * @return a successful GenericListRestApiResponse instance with a null data field
     */
    public static <T> GenericListRestApiResponse<T> success() {
        return success(null);
    }

    /**
     * Creates a failure response with the given code and message.
     *
     * @param <T> the type of elements in the list being encapsulated in the response
     * @param code the code representing the type of error
     * @param message a descriptive message providing more details about the failure
     * @return a {@code GenericListRestApiResponse} indicating the failure, containing the specified error code and message
     */
    public static <T> GenericListRestApiResponse<T> fail(RestApiCode code, String message) {
        GenericListRestApiResponse<T> response = new GenericListRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = code.code();
        return response;
    }

    /**
     * Generates a failed response for a list-based REST API call with the specified error code and message.
     *
     * @param <T> the type of elements in the list being encapsulated in the response
     * @param code the error code as a string
     * @param message the error message to be included in the response
     * @return a GenericListRestApiResponse encapsulating the failure details
     */
    public static <T> GenericListRestApiResponse<T> fail(String code, String message) {
        GenericListRestApiResponse<T> response = new GenericListRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = Integer.parseInt(code);
        return response;
    }
}

package org.venus.admin.support;

import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class GenericRestApiResponse<T> extends AbstractRestApiResponse {
    @Serial
    private static final long serialVersionUID = -3441654766329269405L;

    private T data;

    protected GenericRestApiResponse() {
        super();
    }

    public static <T> GenericRestApiResponse<T> success(T data) {
        GenericRestApiResponse<T> response = new GenericRestApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = VenusRestApiCode.SUCCESS.message();
        response.code = VenusRestApiCode.SUCCESS.code();
        return response;
    }

    public static <T> GenericRestApiResponse<T> success() {
       return success(null);
    }

    public static <T> GenericRestApiResponse<T> fail(VenusRestApiCode code, String message) {
        GenericRestApiResponse<T> response = new GenericRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = code.code();
        return response;
    }

    public static <T> GenericRestApiResponse<T> fail(String code, String message) {
        GenericRestApiResponse<T> response = new GenericRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = Integer.parseInt(code);
        return response;
    }
}

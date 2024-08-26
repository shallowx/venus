package org.venus.admin.support;

import java.io.Serial;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class GenericListRestApiResponse<T> extends AbstractRestApiResponse {
    @Serial
    private static final long serialVersionUID = -6284885253964194599L;

    private List<T> data;

    protected GenericListRestApiResponse() {
        super();
    }

    public static <T> GenericListRestApiResponse<T> success(List<T> data) {
        GenericListRestApiResponse<T> response = new GenericListRestApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = VenusRestApiCode.SUCCESS.message();
        response.code = VenusRestApiCode.SUCCESS.code();
        return response;
    }

    public static <T> GenericListRestApiResponse<T> success() {
       return success(null);
    }

    public static <T> GenericListRestApiResponse<T> fail(VenusRestApiCode code, String message) {
        GenericListRestApiResponse<T> response = new GenericListRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = code.code();
        return response;
    }

    public static <T> GenericListRestApiResponse<T> fail(String code, String message) {
        GenericListRestApiResponse<T> response = new GenericListRestApiResponse<>();
        response.success = false;
        response.message = message;
        response.code = Integer.parseInt(code);
        return response;
    }
}

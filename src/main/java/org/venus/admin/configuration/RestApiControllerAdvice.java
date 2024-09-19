package org.venus.admin.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.venus.admin.annotations.RestApiList;
import org.venus.support.GenericListRestApiResponse;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusException;
import org.venus.support.VenusRestApiCode;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class serves as a centralized exception handler for REST API controllers.
 * It uses Spring's {@link ControllerAdvice} to provide global exception handling
 * across all controllers in the application. It defines methods to handle various
 * types of exceptions that may occur during the processing of REST API requests.
 */
@Slf4j
@RestControllerAdvice
public class RestApiControllerAdvice {
    /**
     * Handles exceptions of type MethodArgumentNotValidException by extracting error details
     * and returning an appropriate error response based on the invoked handler method.
     *
     * @param ex the MethodArgumentNotValidException thrown during validation failure
     * @param request the HttpServletRequest associated with the validation failure
     * @return a response object encapsulating the error details, formatted based on
     *         whether the endpoint is annotated with RestApiList
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String[] errors = new String[3];
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors[0] = ((FieldError) error).getField();
            errors[1] = error.getDefaultMessage();
            errors[2] = Objects.toString(ex.getBody().getStatus(), "500");
        });
        if (log.isErrorEnabled()) {
            log.error("path:{} - {}", request.getRequestURL(), errors[1]);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        String format = String.format("field[%s] - %s", errors[0], errors[1]);
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail(errors[2], format);
        } else {
            return GenericRestApiResponse.fail(errors[2], format);
        }
    }

    /**
     * Handles exceptions of type ConstraintViolationException, typically resulting from validation errors.
     * This method extracts all validation error messages and consolidates them into a single error message.
     * It also logs the error along with the request URL.
     *
     * @param ex the ConstraintViolationException that was thrown
     * @param request the HttpServletRequest during which the exception was thrown
     * @return a response entity containing the error message and appropriate status code
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(", "));
        if (log.isErrorEnabled()) {
            log.error("path:{} - {}", request.getRequestURL(), errorMessage);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, errorMessage);
        } else {
            return GenericRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, errorMessage);
        }
    }

    /**
     * Exception handler method for handling cases where an HTTP message is not readable.
     *
     * @param ex the HttpMessageNotReadableException thrown when the HTTP message cannot be read
     * @param request the HttpServletRequest object representing the client request
     * @return an appropriate response object indicating a failure due to a bad request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        if (log.isErrorEnabled()) {
            log.error("path:{} - not readable exception", request.getRequestURL(), ex);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        String message = "Required request body is missing";
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, message);
        } else {
            return GenericRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, message);
        }
    }

    /**
     * Handles MethodArgumentNotValidException thrown during validation failure.
     *
     * @param e the MethodArgumentNotValidException caught during the validation process
     * @return a GenericRestApiResponse indicating validation failure and a BAD_REQUEST status code
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handle(MissingServletRequestParameterException e) {
        if (log.isErrorEnabled()) {
            log.error("Method argument not valid exception", e);
        }
        return GenericRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, "Method argument not valid exception");
    }


    /**
     * Handles exceptions of type VenusException.
     *
     * @param request the HttpServletRequest object containing client request details
     * @param e the VenusException object caught by the handler
     * @return a structured response indicating the failure, formatted as either GenericListRestApiResponse
     *         or GenericRestApiResponse, based on the presence of RestApiList annotation on the handler method
     */
    @ExceptionHandler(value = VenusException.class)
    public Object venusExceptionHandler(HttpServletRequest request, VenusException e) {
        if (log.isErrorEnabled()) {
            log.error("path:{} - venus exception", request.getRequestURL(), e);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, e.getMessage());
        } else {
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, e.getMessage());
        }
    }

    /**
     * Handles exceptions thrown during the handling of web requests.
     *
     * @param request the HttpServletRequest that resulted in the exception
     * @param e the exception that was thrown
     * @return an object representing a generic failure response
     */
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(HttpServletRequest request, Exception e) {
        if (log.isErrorEnabled()) {
            log.error("path:{} - bad request", request.getRequestURL(), e);
        }
        return GenericRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, VenusRestApiCode.BAD_REQUEST.message());
    }
}

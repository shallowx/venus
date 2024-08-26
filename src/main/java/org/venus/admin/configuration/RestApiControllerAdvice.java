package org.venus.admin.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.venus.admin.annotations.RestApiList;
import org.venus.admin.support.GenericListRestApiResponse;
import org.venus.admin.support.GenericRestApiResponse;
import org.venus.admin.support.VenusAdminException;
import org.venus.admin.support.VenusRestApiCode;
@Slf4j
@ControllerAdvice
public class RestApiControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String[] errors = new String[3];
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors[0] = field;

            String message = error.getDefaultMessage();
            errors[1] = message;

            String code = Objects.toString(error.getCode(), "500");
            errors[2] = code;
        });

        if (log.isErrorEnabled()) {
            log.error("path:{} - {}", request.getRequestURI(), errors[1]);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
           return GenericListRestApiResponse.fail(errors[2], String.format("%s:%s", errors[0], errors[1]));
        } else {
           return GenericRestApiResponse.fail(errors[2], String.format("%s:%s", errors[0], errors[1]));
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(", "));
        if (log.isErrorEnabled()) {
            log.error("path:{} - {}", request.getRequestURI(), errorMessage);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail("400", errorMessage);
        } else {
            return GenericRestApiResponse.fail("400", errorMessage);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        if (log.isErrorEnabled()) {
            log.error("path:{} - not readable exception", request.getRequestURI(), ex);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        String message = "Required request body is missing";
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, message);
        } else {
            return GenericRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, message);
        }
    }


    @ExceptionHandler(value =VenusAdminException.class)
    @ResponseBody
    public Object venusAdminEexceptionHandler(HttpServletRequest request, VenusAdminException e){
        if (log.isErrorEnabled()) {
            log.error("path:{} - venus admin exception", request.getRequestURI(), e);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, e.getMessage());
        } else {
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, e.getMessage());
        }
    }

    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request, Exception e){
        if (log.isErrorEnabled()) {
            log.error("path:{} - bad request", request.getRequestURL(), e);
        }
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null && handlerMethod.hasMethodAnnotation(RestApiList.class)) {
            return GenericListRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, VenusRestApiCode.BAD_REQUEST.message());
        } else {
            return GenericRestApiResponse.fail(VenusRestApiCode.BAD_REQUEST, VenusRestApiCode.BAD_REQUEST.message());
        }
    }
}

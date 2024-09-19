package org.venus.openapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusRestApiCode;

/**
 * A REST controller for handling geolocation reporting endpoints in the OpenAPI service.
 *
 * This controller provides an API endpoint to report geolocation data.
 *
 * Annotations:
 * - `@RestController`: Indicates that the class serves RESTful web services.
 * - `@RequestMapping`: Maps HTTP requests to handler methods of the MVC and REST controllers.
 * - `@Slf4j`: Lombok annotation for logging.
 */
@RestController
@RequestMapping("/openapi/geo")
@Slf4j
public class OpenapiGeoReportRestController {

    /**
     * Service interface used to report geolocation data within the OpenAPI service.
     *
     * This variable represents the primary service interface for handling geolocation reports.
     * It is automatically injected into the `OpenapiGeoReportRestController` class by Spring's dependency injection mechanism.
     *
     * Annotations:
     * - `@Autowired`: This annotation allows Spring to automatically inject an instance of `IOpenapiGeoService` into the controller.
     */
    @Autowired
    private IOpenapiGeoService iOpenapiGeoService;

    /**
     * Reports geolocation data provided in the request.
     *
     * @param request The request payload containing geolocation information to be reported. Must be a valid instance of {@link OpenapiGeoRequest}.
     * @return A {@link GenericRestApiResponse} object. Returns a success response if the operation is successful, and a failure response with an error code and message if an exception
     *  occurs.
     */
    @PostMapping("/report")
    public GenericRestApiResponse<Void> report(@RequestBody @Validated OpenapiGeoRequest request) {
        try {
            iOpenapiGeoService.report(OpenapiGeoEntity.from(request));
            return GenericRestApiResponse.success();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Openapi report geo data failure");
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.FAILURE, VenusRestApiCode.FAILURE.message());
        }
    }
}

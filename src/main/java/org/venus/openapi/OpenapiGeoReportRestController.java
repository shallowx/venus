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
@RequestMapping("/v1/openapi/geo")
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
     * Handles HTTP POST requests to report geolocation data.
     *
     * @param request the OpenapiGeoRequest object containing the geolocation data to be reported
     * @return a GenericRestApiResponse containing a Boolean indicating success (true) or failure (false) of the operation
     */
    @PostMapping("/report")
    public GenericRestApiResponse<Boolean> report(@RequestBody @Validated OpenapiGeoRequest request) {
        try {
            return GenericRestApiResponse.success(iOpenapiGeoService.report(OpenapiGeoEntity.from(request)));
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Openapi report geo data failure");
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.OPENAPI_GEO_EXCEPTION, VenusRestApiCode.OPENAPI_GEO_EXCEPTION.message("Openapi report geo data failure \n" + e.getMessage()));
        }
    }
}

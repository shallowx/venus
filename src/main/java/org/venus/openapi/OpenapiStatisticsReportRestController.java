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
 * OpenapiStatisticsReportRestController is a REST controller for handling OpenAPI statistics reporting.
 * It exposes an endpoint for reporting statistics data.
 *
 * An instance of IOpenapiStatisticsReportService is used to process the reporting logic.
 */
@RestController
@RequestMapping("/openapi/v1/statistics")
@Slf4j
public class OpenapiStatisticsReportRestController {

    /**
     * An instance of IOpenapiStatisticsReportService that handles the processing
     * of OpenAPI statistics reporting.
     *
     * This service is used within the OpenapiStatisticsReportRestController to
     * manage and process the logic related to reporting statistics data.
     *
     * The service is injected using the @Autowired annotation, allowing it to
     * automatically be wired into the controller by the Spring framework.
     */
    @Autowired
    private IOpenapiStatisticsReportService iOpenapiStatisticsReportService;

    /**
     * Handles the reporting of OpenAPI statistics data.
     *
     * @param request the OpenapiStatisticsRequest object containing the statistics data to be reported
     * @return a GenericRestApiResponse containing a Boolean indicating the success or failure of the reporting operation
     */
    @PostMapping("/report")
    public GenericRestApiResponse<Boolean> report(@RequestBody @Validated OpenapiStatisticsRequest request) {
        try {
            return GenericRestApiResponse.success(iOpenapiStatisticsReportService.report(OpenapiStatisticsEntity.from(request)));
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Openapi report statistics data failure");
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.OPENAPI_STATISTICS_EXCEPTION, VenusRestApiCode.OPENAPI_STATISTICS_EXCEPTION.message("Openapi report statistics data failure \n" + e.getMessage()));
        }
    }
}

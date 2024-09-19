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
@RequestMapping("/openapi/statistics")
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
     * Handles the reporting of OpenAPI statistics by accepting a valid {@link OpenapiStatisticsRequest} payload,
     * processing it, and returning a generic response indicating the success or failure of the operation.
     *
     * @param request the OpenapiStatisticsRequest containing details for the statistics report
     * @return a GenericRestApiResponse indicating the success or failure of the report operation
     */
    @PostMapping("/report")
    public GenericRestApiResponse<Void> report(@RequestBody @Validated OpenapiStatisticsRequest request) {
        try {
            iOpenapiStatisticsReportService.report(OpenapiStatisticsEntity.from(request));
            return GenericRestApiResponse.success();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Openapi report statistics data failure");
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.FAILURE, VenusRestApiCode.FAILURE.message());
        }
    }
}

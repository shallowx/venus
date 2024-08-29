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

import java.util.List;

@RestController
@RequestMapping("/openapi/statistics")
@Slf4j
public class OpenapiStatisticsReportRestController {

    @Autowired
    private IOpenapiStatisticsReportService iOpenapiStatisticsReportService;

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

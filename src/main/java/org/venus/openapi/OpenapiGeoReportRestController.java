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

@RestController
@RequestMapping("/openapi/geo")
@Slf4j
public class OpenapiGeoReportRestController {

    @Autowired
    private IOpenapiGeoService iOpenapiGeoService;

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

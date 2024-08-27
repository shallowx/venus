package org.venus.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.domain.GeoResponse;
import org.venus.admin.service.IGeoService;
import org.venus.support.GenericListRestApiResponse;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusRestApiCode;

@RestController
@RequestMapping("/geo")
@Slf4j
public class GeoRestController {

    @Autowired
    private IGeoService iGeoService;

    @GetMapping("/lists")
    public GenericListRestApiResponse<GeoResponse> lists() {
        try {
            return GenericListRestApiResponse.success(
                    GeoResponse.from(iGeoService.lists())
            );
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error listing links", e);
            }
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    @GetMapping("/detail/{id}")
    public GenericRestApiResponse<GeoResponse> detail(@PathVariable long id) {
        try {
            return GenericRestApiResponse.success(
                    GeoResponse.from(iGeoService.detail(id))
            );
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error get geo's detail", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }
}

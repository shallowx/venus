package org.venus.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.domain.StatisticsResponse;
import org.venus.admin.service.IStatisticsService;
import org.venus.support.GenericListRestApiResponse;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusRestApiCode;

@RestController
@RequestMapping(value = "/statistics")
@Slf4j
public class StatisticsRestController {

    @Autowired
    private IStatisticsService iStatisticsService;

    @GetMapping("/lists")
    public GenericListRestApiResponse<StatisticsResponse> lists() {
        try {
            return GenericListRestApiResponse.success(
                    StatisticsResponse.from(iStatisticsService.lists())
            );
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error listing statistics", e);
            }
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    @GetMapping("/detail/{id}")
    public GenericRestApiResponse<StatisticsResponse> detail(@PathVariable long id) {
        try {
            return GenericRestApiResponse.success(
                    StatisticsResponse.from(iStatisticsService.detail(id))
            );
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error get statistics detail", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }
}

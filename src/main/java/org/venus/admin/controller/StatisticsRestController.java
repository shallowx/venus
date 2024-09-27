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

/**
 * REST controller for handling statistics-related endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/statistics")
@Slf4j
public class StatisticsRestController {

    /**
     * Service for performing operations related to statistics.
     *
     * This service provides methods to retrieve statistics data
     * for lists and specific details.
     */
    @Autowired
    private IStatisticsService iStatisticsService;

    /**
     * Handles the HTTP GET request to retrieve a list of statistics.
     *
     * @return a response object containing a list of StatisticsResponse objects if successful,
     *         or an error response if an exception occurs.
     */
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
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_STATISTICS_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_STATISTICS_EXCEPTION.message("Listing statistics failed \n" + e.getMessage()));
        }
    }

    /**
     * Handles the GET request for retrieving the details of a specific statistic.
     *
     * @param id the unique identifier of the statistic to retrieve
     * @return a {@link GenericRestApiResponse} containing the {@link StatisticsResponse} or an error message in case of failure
     */
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
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_STATISTICS_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_STATISTICS_EXCEPTION.message("Get statistics detail failed \n" + e.getMessage()));
        }
    }
}

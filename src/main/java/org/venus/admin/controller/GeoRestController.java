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

/**
 * REST controller for handling geographical data.
 */
@RestController
@RequestMapping("/api/v1/geo")
@Slf4j
public class GeoRestController {

    /**
     * Service interface for handling geographical data operations.
     *
     * This interface provides methods to retrieve lists of geographical entities
     * and to get detailed information about a specific geographical entity.
     *
     * <p>Injected into controller classes to delegate requests related to
     * geographical data.
     */
    @Autowired
    private IGeoService iGeoService;

    /**
     * Retrieves a list of geographical data entries.
     *
     * @return a response containing the list of geographical data entries or an error response if an exception occurs
     */
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
            String error = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                error = cause.getMessage();
            }
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, error == null ? e.getMessage(): error);
        }
    }

    /**
     * Retrieves the details of a geographical entity by its ID.
     *
     * @param id the ID of the geographical entity to retrieve
     * @return a GenericRestApiResponse containing the details of the geographical entity.
     *         If an error occurs, returns a failure response with an appropriate error code and message.
     */
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
            String error = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                error = cause.getMessage();
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, error == null ? e.getMessage(): error);
        }
    }
}

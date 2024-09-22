package org.venus.admin.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.venus.admin.annotations.RestApiList;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.domain.LinksRequest;
import org.venus.admin.domain.LinksResponse;
import org.venus.admin.service.ILinksService;
import org.venus.support.GenericListRestApiResponse;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusRestApiCode;

/**
 * `LinksRestController` provides REST API endpoints for managing link resources.
 * It supports operations such as listing links, retrieving link details,
 * adding new links, updating existing links, and deleting links.
 */
@RestController
@RequestMapping(value = "/links")
@Slf4j
public class LinksRestController {

    /**
     * An instance of ILinksService, which is injected using Spring's @Autowired annotation.
     * This service is responsible for handling operations related to links within the application.
     */
    @Autowired
    private ILinksService iLinksService;

    /**
     * Endpoint to retrieve a list of links.
     *
     * @return A {@link GenericListRestApiResponse} containing a list of {@link LinksResponse}
     *         objects if the retrieval is successful. If an exception occurs, returns
     *         a failed response with the appropriate error code and message.
     */
    @RestApiList
    @GetMapping("/lists")
    public GenericListRestApiResponse<LinksResponse> lists() {
        try {
            return GenericListRestApiResponse.success(
                    LinksResponse.from(
                            iLinksService.lists()
                    )
            );
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error listing links", e);
            }
            return GenericListRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    /**
     * Retrieve the details of a link specified by the given ID.
     *
     * @param id The ID of the link to retrieve. Must be a non-negative long value.
     * @return A GenericRestApiResponse containing a LinksResponse with the link details if successful,
     *         or a failure response with an error code and message if an exception occurs.
     */
    @GetMapping("/detail/{id}")
    public GenericRestApiResponse<LinksResponse> detail(@PathVariable
                                                        @Validated
                                                        @NotNull
                                                        @Min(0)
                                                        @Max(Long.MAX_VALUE) long id) {
        try {
            return GenericRestApiResponse.success(
                    LinksResponse.from(
                            iLinksService.get(id)
                    )
            );
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error get link's detail", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    /**
     * Adds a new link entity to the service.
     *
     * @param request  the LinksRequest object containing the details of the link to be added
     * @return a GenericRestApiResponse object indicating the success or failure of the operation
     */
    @PostMapping("/add")
    public GenericRestApiResponse<Void> add(@RequestBody @Validated LinksRequest request) {
        try {
            LinksDao ld = LinksDao.fromEntity(request);
            iLinksService.add(ld);
            return GenericRestApiResponse.success();
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.error("Add Links failure", e);
            }

            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    /**
     * Updates the link information based on the provided request.
     *
     * @param request the request object containing the link data to be updated
     * @return a GenericRestApiResponse indicating the success or failure of the update operation
     */
    @PostMapping("/update")
    public GenericRestApiResponse<Long> update(@RequestBody @Validated LinksRequest request) {
        try {
            LinksDao ld = LinksDao.fromEntity(request);
            iLinksService.update(ld);
            return GenericRestApiResponse.success(ld.getId());
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.error("Update Links failure", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    /**
     * Deletes a link with the specified ID.
     *
     * @param id The ID of the link to be deleted. Must be a non-null value,
     *           greater than or equal to 0 and less than or equal to Long.MAX_VALUE.
     * @return A GenericRestApiResponse indicating the result of the delete operation.
     *         Returns success if the deletion is successful, otherwise returns a failure response.
     */
    @DeleteMapping("/delete/{id}")
    public GenericRestApiResponse<Long> delete(@PathVariable
                                               @Validated
                                               @NotNull
                                               @Min(0)
                                               @Max(Long.MAX_VALUE) long id) {
        try {
            iLinksService.delete(id);
            return GenericRestApiResponse.success(id);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Delete links[id:{}] failure", id, e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }
}

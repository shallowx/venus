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

@RestController
@RequestMapping(value = "/links")
@Slf4j
public class LinksRestController {

    @Autowired
    private ILinksService iLinksService;

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

    @GetMapping("/detail/{id}")
    public GenericRestApiResponse<LinksResponse> detail(@PathVariable @NotNull
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

    @PostMapping("/add")
    public GenericRestApiResponse<Void> add(@RequestBody @Validated LinksRequest request) {
        try {
            LinksDao ld = LinksDao.fromEntity(request);
            iLinksService.add(ld);
            return GenericRestApiResponse.success();
        }catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.error("Add Links failure", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    @PostMapping("/update")
    public GenericRestApiResponse<Void> update(@RequestBody @Validated LinksRequest request) {
        try {
            LinksDao ld = LinksDao.fromEntity(request);
            iLinksService.update(ld);
            return GenericRestApiResponse.success();
        }catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.error("Update Links failure", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    @DeleteMapping("/delete/{id}")
    public GenericRestApiResponse<Void> delete(@PathVariable
                                                   @Validated
                                                   @NotNull
                                                   @Min(0)
                                                   @Max(Long.MAX_VALUE) long id) {
        try {
            iLinksService.delete(id);
            return GenericRestApiResponse.success();
        }catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Delete links[id:{}] failure", id, e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

}

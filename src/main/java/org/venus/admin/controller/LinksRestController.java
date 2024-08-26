package org.venus.admin.controller;

import static org.venus.admin.support.GenericRestApiResponse.success;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.annotations.RestApiList;
import org.venus.admin.domain.Links;
import org.venus.admin.domain.LinksDao;
import org.venus.admin.domain.LinksRequest;
import org.venus.admin.domain.LinksResponse;
import org.venus.admin.service.ILinksService;
import org.venus.admin.support.GenericListRestApiResponse;
import org.venus.admin.support.GenericRestApiResponse;
import org.venus.admin.support.VenusRestApiCode;

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
            return GenericListRestApiResponse.fail(VenusRestApiCode.FAILURE, VenusRestApiCode.FAILURE.message());
        }
    }

    @PostMapping("/add")
    public GenericRestApiResponse<Void> add(@RequestBody @Validated LinksRequest request) {
        try {
            LinksDao ld = LinksDao.fromEntity(request);
            iLinksService.add(ld);
            return success();
        }catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.error("Add Links failure", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.FAILURE, VenusRestApiCode.FAILURE.message());
        }
    }


}

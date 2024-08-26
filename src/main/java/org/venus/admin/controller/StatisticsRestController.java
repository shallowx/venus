package org.venus.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.domain.Statistics;
import org.venus.admin.support.GenericListRestApiResponse;

@RestController
@RequestMapping(value = "/statistics")
public class StatisticsRestController {

    @GetMapping("/lists")
    public GenericListRestApiResponse<Statistics> lists() {
        return null;
    }
}

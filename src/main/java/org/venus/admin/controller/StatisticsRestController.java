package org.venus.admin.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.entity.Statistics;
import org.venus.admin.support.AbstractRestApiResponse;
import org.venus.admin.support.GenericListRestApiResponse;

@RestController
@RequestMapping(value = "statistics/")
public class StatisticsRestController {

    @GetMapping("lists")
    public GenericListRestApiResponse<Statistics> lists() {
        return null;
    }
}

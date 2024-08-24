package org.venus.admin.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venus.admin.entiy.Statistics;
import org.venus.admin.support.RestApiResponse;

@RestController(value = "statistics/")
public class StatisticsRestController {

    @GetMapping("lists")
    public RestApiResponse<List<Statistics>> lists() {
        return null;
    }
}

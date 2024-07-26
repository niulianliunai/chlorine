package com.chlorine.app.packages.controller;

import com.chlorine.app.packages.service.StatisticsService;
import com.chlorine.base.mvc.dto.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("Statistics")
public class StatisticsController  {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("statisticsWareHousePackageSource")
    public CommonResult<Map<String, Object>> statisticsWareHousePackageSource() {
        return CommonResult.success(statisticsService.statisticsWareHousePackageSource());
    }
}

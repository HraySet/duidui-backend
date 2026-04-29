package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<?> stats() {
        return dashboardService.stats();
    }

    @GetMapping("/trend")
    public Result<?> trend() {
        return dashboardService.trend();
    }
}

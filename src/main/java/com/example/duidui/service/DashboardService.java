package com.example.duidui.service;

import com.example.duidui.common.Result;

public interface DashboardService {
    Result<?> stats();
    Result<?> trend();
}

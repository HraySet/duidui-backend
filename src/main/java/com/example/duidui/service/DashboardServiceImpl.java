package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.mapper.DashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public Result<?> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("productCount", dashboardMapper.productCount());
        stats.put("totalStock", dashboardMapper.totalStock());
        stats.put("todayInCount", dashboardMapper.todayInCount());
        stats.put("todayOutCount", dashboardMapper.todayOutCount());
        stats.put("lowStockCount", dashboardMapper.lowStockCount());
        stats.put("monthInQty", dashboardMapper.monthInQty());
        return Result.success(stats);
    }

    @Override
    public Result<?> trend() {
        return Result.success(dashboardMapper.trend());
    }
}

package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.entity.Warehouse;

public interface WarehouseService {
    Result<?> list();
    Result<?> add(Warehouse warehouse);
    Result<?> update(Warehouse warehouse);
    Result<?> delete(Long id);
}

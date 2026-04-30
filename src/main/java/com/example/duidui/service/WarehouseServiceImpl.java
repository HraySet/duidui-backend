package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.entity.Warehouse;
import com.example.duidui.mapper.WarehouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public Result<?> list() {
        return Result.success(warehouseMapper.selectList(null));
    }

    @Override
    public Result<?> add(Warehouse warehouse) {
        if (!StringUtils.hasText(warehouse.getName())) {
            return Result.error("仓库名称不能为空");
        }
        warehouseMapper.insert(warehouse);
        return Result.success();
    }

    @Override
    public Result<?> update(Warehouse warehouse) {
        if (warehouse.getId() == null) {
            return Result.error("仓库ID不能为空");
        }
        warehouseMapper.updateById(warehouse);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        if (id == null) {
            return Result.error("仓库ID不能为空");
        }
        warehouseMapper.deleteById(id);
        return Result.success();
    }
}

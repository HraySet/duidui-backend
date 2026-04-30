package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.entity.Warehouse;
import com.example.duidui.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/list")
    public Result<?> list() {
        return warehouseService.list();
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Warehouse warehouse) {
        return warehouseService.add(warehouse);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Warehouse warehouse) {
        return warehouseService.update(warehouse);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return warehouseService.delete(id);
    }
}

package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.entity.Product;
import com.example.duidui.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public Result<?> add(@RequestBody Product product) {
        return productService.add(product);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Product product) {
        return productService.update(product);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return productService.delete(id);
    }

    @GetMapping("/page")
    public Result<?> page(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return productService.page(pageNum, pageSize, keyword);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return productService.getById(id);
    }
}
package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.entity.Product;
import java.util.List;

public interface ProductService {
    Result<?> add(Product product);
    Result<?> update(Product product);
    Result<?> delete(Long id);
    Result<Product> getById(Long id);
    Result<List<Product>> list(String keyword);  // 模糊查询名称或SKU
    Result<?> page(int pageNum, int pageSize, String keyword);
}
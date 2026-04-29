package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.entity.Product;
import java.util.List;

public interface ProductService {
    Result<?> add(Product product);
    Result<?> update(Product product);
    Result<?> delete(Long id);
    Result<Product> getById(Long id);
    Result<List<Product>> list(String keyword);
    Result<?> page(int pageNum, int pageSize, String keyword);
    Result<?> lowStock();
}
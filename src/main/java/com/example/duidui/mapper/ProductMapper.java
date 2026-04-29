package com.example.duidui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.duidui.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT p.id, p.name, p.sku, p.category, p.low_stock_threshold as lowStockThreshold, " +
            "COALESCE(s.quantity, 0) as stock " +
            "FROM product p " +
            "LEFT JOIN stock s ON p.id = s.product_id " +
            "WHERE p.status = 1 AND p.low_stock_threshold > 0 " +
            "AND COALESCE(s.quantity, 0) <= p.low_stock_threshold " +
            "ORDER BY p.name")
    List<Map<String, Object>> selectLowStock();
}

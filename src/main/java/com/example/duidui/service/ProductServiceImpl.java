package com.example.duidui.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.duidui.common.Result;
import com.example.duidui.entity.Product;
import com.example.duidui.mapper.ProductMapper;
import com.example.duidui.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<?> add(Product product) {
        // 1. SKU 唯一性校验
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            return Result.error("商品编码不能为空");
        }
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("sku", product.getSku());
        if (productMapper.selectCount(wrapper) > 0) {
            return Result.error("商品编码已存在");
        }
        // 2. 设置默认值（如果前端没传）
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getLowStockThreshold() == null) product.setLowStockThreshold(0);
        // 3. 插入
        int rows = productMapper.insert(product);
        return rows > 0 ? Result.success() : Result.error("添加失败");
    }
    @Override
    public Result<?> page(int pageNum, int pageSize, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like("name", keyword).or().like("sku", keyword);
        }

        wrapper.orderByDesc("created_at");

        Page<Product> result = productMapper.selectPage(page, wrapper);

        return Result.success(result);
    }

    @Override
    public Result<?> update(Product product) {
        if (product.getId() == null) {
            return Result.error("商品ID不能为空");
        }
        // 如果修改了SKU，要校验唯一性（排除自身）
        if (StringUtils.hasText(product.getSku())) {
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("sku", product.getSku());
            wrapper.ne("id", product.getId());
            if (productMapper.selectCount(wrapper) > 0) {
                return Result.error("商品编码已存在");
            }
        }
        int rows = productMapper.updateById(product);
        return rows > 0 ? Result.success() : Result.error("更新失败");
    }

    @Override
    public Result<?> delete(Long id) {
        if (id == null) {
            return Result.error("ID不能为空");
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }

        // TODO: 判断是否有库存/出入库记录
        // if (hasRecord) return Result.error("已有记录，不能删除");

        int rows = productMapper.deleteById(id);
        return rows > 0 ? Result.success() : Result.error("删除失败");
    }

    @Override
    public Result<Product> getById(Long id) {
        Product product = productMapper.selectById(id);
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }

    @Override
    public Result<List<Product>> list(String keyword) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("name", keyword).or().like("sku", keyword);
        }
        wrapper.orderByDesc("created_at");
        List<Product> list = productMapper.selectList(wrapper);
        return Result.success(list);
    }
}
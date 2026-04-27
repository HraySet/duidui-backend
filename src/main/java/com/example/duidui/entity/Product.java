package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;  // 商品ID

    private String name;  // 商品名称

    private String category;  // 商品分类

    private String sku;  // 商品编码

    private BigDecimal price;   // 单价（用于入库/出库时的金额统计）

    private Integer lowStockThreshold;   // 库存预警阈值（低于此值提醒）

    private String unit;  // 单位

    private Integer status; // 0-停用，1-启用，默认1

    private String description;  // 描述

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;  // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;  // 更新时间
}

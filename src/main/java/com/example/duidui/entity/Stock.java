package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock")
public class Stock {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;
    private Long warehouseId;
    private Integer quantity;

    // 乐观锁
    private Integer version;

    private LocalDateTime updateTime;
}
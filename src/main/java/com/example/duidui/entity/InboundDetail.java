package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("inbound_detail")
public class InboundDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inboundId;    // 入库单ID
    private Long productId;    // 商品ID
    private Integer quantity;  // 入库数量
    private BigDecimal price;  // 入库单价
}
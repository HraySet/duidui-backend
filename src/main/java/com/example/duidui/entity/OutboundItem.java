package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("outbound_item")
public class OutboundItem {

    @TableId(type = IdType.AUTO)
    private Long id;  // 明细ID

    private Long outboundId;  // 出库单ID

    private Long itemId;  // 商品ID

    private Integer quantity;  // 出库数量
}

package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("inbound_item")
public class InboundItem {

    @TableId(type = IdType.AUTO)
    private Long id;  // 明细ID

    private Long inboundId;  // 入库单ID

    private Long itemId;  // 商品ID

    private Integer quantity;  // 入库数量
}

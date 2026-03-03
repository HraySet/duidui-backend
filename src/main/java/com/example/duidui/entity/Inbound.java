package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inbound")
public class Inbound {

    @TableId(type = IdType.AUTO)
    private Long id;  // 入库单ID

    private String inboundNo;  // 入库单编号

    private String supplier;  // 供应商

    private Integer totalQuantity;  // 总数量

    private String status;  // 状态：PENDING / COMPLETED

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;  // 创建时间
}

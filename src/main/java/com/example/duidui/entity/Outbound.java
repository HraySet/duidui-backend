package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("outbound")
public class Outbound {

    @TableId(type = IdType.AUTO)
    private Long id;  // 出库单ID

    private String outboundNo;  // 出库单编号

    private String customer;  // 客户名称

    private Integer totalQuantity;  // 总数量

    private String status;  // 状态：PENDING / COMPLETED

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;  // 创建时间
}

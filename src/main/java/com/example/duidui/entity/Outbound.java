package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName("outbound")
public class Outbound {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String outboundNo;
    private String customer;
    private Integer totalQuantity;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 明细列表（不入库，仅返回用） */
    @TableField(exist = false)
    private List<Map<String, Object>> items;
}

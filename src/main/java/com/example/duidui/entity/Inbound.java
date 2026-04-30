package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName("inbound")
public class Inbound {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String inboundNo;
    private String supplier;
    private Integer totalQuantity;
    private String status;
    private Long warehouseId;
    private Long operatorId;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 明细列表（不入库，仅返回用） */
    @TableField(exist = false)
    private List<Map<String, Object>> items;
}

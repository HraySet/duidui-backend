package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inbound")
public class Inbound {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String inboundNo;        // 入库单号
    private String supplier;         // 供应商
    private Integer totalQuantity;   // 总数量
    private String status;           // 状态 PENDING(待完成)/COMPLETED(已完成)

    private Long warehouseId;        // 新增：仓库ID
    private Long operatorId;         // 新增：操作人ID
    private String remark;           // 新增：备注

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;  // 用户ID

    private String username;  // 用户名

    private String password;  // 密码（加密）

    private String role;  // 角色：ADMIN / USER

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;  // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;  // 更新时间
}

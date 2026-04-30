package com.example.duidui.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("settings")
public class Settings {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keyName;
    private String keyValue;
}

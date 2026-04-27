package com.example.duidui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.duidui.entity.Stock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {
}
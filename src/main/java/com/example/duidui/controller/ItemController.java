package com.example.duidui.controller;

import com.example.duidui.entity.Item;
import com.example.duidui.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")

public class ItemController {

    @Autowired
    private ItemMapper itemMapper;

    // 查询所有物品
    @GetMapping
    public List<Item> list() {
        return itemMapper.selectList(null);
    }


    @GetMapping("/hello")
    public String hello() {
        return "你好，我是堆堆！";
    }



}
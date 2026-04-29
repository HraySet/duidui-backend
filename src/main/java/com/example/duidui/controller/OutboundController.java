package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.dto.OutboundRequest;
import com.example.duidui.service.OutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outbound")
public class OutboundController {
    
    @Autowired
    private OutboundService outboundService;
    
    @PostMapping
    public Result<?> create(@RequestBody OutboundRequest request) {
        return outboundService.createOutbound(request);
    }

    @GetMapping("/page")
    public Result<?> page(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return outboundService.page(pageNum, pageSize, keyword);
    }
}

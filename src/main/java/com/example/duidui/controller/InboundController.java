package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.dto.InboundRequest;
import com.example.duidui.service.InboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inbound")
public class InboundController {

    @Autowired
    private InboundService inboundService;

    /**
     * 创建入库单
     */
    @PostMapping
    public Result<?> create(@RequestBody InboundRequest request) {

        // TODO: 后续改为从 token 获取当前登录用户ID
        Long operatorId = 1L;

        return inboundService.createInbound(request, operatorId);
    }
}

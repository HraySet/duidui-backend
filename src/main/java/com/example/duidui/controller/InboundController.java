package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.dto.InboundRequest;
import com.example.duidui.service.InboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inbound")
public class InboundController {

    @Autowired
    private InboundService inboundService;

    /**
     * 创建入库单（待审批）
     */
    @PostMapping
    public Result<?> create(@RequestBody InboundRequest request) {
        Long operatorId = 1L; // TODO: 从 token 获取
        return inboundService.createInbound(request, operatorId);
    }

    /**
     * 入库单分页列表
     */
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return inboundService.page(pageNum, pageSize, keyword);
    }

    /**
     * 审批通过
     */
    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id) {
        return inboundService.approve(id);
    }

    /**
     * 驳回
     */
    @PutMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return inboundService.reject(id, body.get("reason"));
    }
}

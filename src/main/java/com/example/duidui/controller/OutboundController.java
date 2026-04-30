package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.dto.OutboundRequest;
import com.example.duidui.service.OutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/outbound")
public class OutboundController {

    @Autowired
    private OutboundService outboundService;

    /**
     * 创建出库单（待审批）
     */
    @PostMapping
    public Result<?> create(@RequestBody OutboundRequest request) {
        return outboundService.createOutbound(request);
    }

    /**
     * 出库单分页列表
     */
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return outboundService.page(pageNum, pageSize, keyword);
    }

    /**
     * 审批通过
     */
    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id) {
        return outboundService.approve(id);
    }

    /**
     * 驳回
     */
    @PutMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return outboundService.reject(id, body.get("reason"));
    }
}

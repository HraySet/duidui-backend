package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.dto.OutboundRequest;

public interface OutboundService {
    Result<?> createOutbound(OutboundRequest request);
    Result<?> page(int pageNum, int pageSize, String keyword);
    Result<?> approve(Long id);
    Result<?> reject(Long id, String reason);
}

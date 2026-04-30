package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.dto.InboundRequest;

public interface InboundService {
    Result<?> createInbound(InboundRequest request, Long operatorId);
    Result<?> page(int pageNum, int pageSize, String keyword);
    Result<?> approve(Long id);
    Result<?> reject(Long id, String reason);
}

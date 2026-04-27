package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.dto.OutboundRequest;

public interface OutboundService {
    Result<?> createOutbound(OutboundRequest request);
}

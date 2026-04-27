package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.dto.InboundRequest;


public interface InboundService {
    Result<?> createInbound(InboundRequest request, Long operatorId);
}
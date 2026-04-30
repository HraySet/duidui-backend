package com.example.duidui.service;

import com.example.duidui.common.Result;

import java.util.Map;

public interface SettingsService {
    Result<?> saveNumberFormat(Map<String, Object> body);
    Map<String, String> loadAll();
    /** 根据规则生成单号 */
    String generateInboundNo();
    String generateOutboundNo();
}

package com.example.duidui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.duidui.common.Result;
import com.example.duidui.entity.Inbound;
import com.example.duidui.entity.Settings;
import com.example.duidui.mapper.InboundMapper;
import com.example.duidui.mapper.SettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired private SettingsMapper settingsMapper;
    @Autowired private InboundMapper inboundMapper;

    @Override
    public Result<?> saveNumberFormat(Map<String, Object> body) {
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            QueryWrapper<Settings> qw = new QueryWrapper<>();
            qw.eq("key_name", entry.getKey());
            Settings s = settingsMapper.selectOne(qw);
            if (s == null) {
                s = new Settings();
                s.setKeyName(entry.getKey());
                s.setKeyValue(String.valueOf(entry.getValue()));
                settingsMapper.insert(s);
            } else {
                s.setKeyValue(String.valueOf(entry.getValue()));
                settingsMapper.updateById(s);
            }
        }
        return Result.success("保存成功");
    }

    @Override
    public Map<String, String> loadAll() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Settings s : settingsMapper.selectList(null)) {
            map.put(s.getKeyName(), s.getKeyValue());
        }
        // 默认值
        map.putIfAbsent("inboundPrefix", "IN");
        map.putIfAbsent("inboundDateFmt", "yyyyMMdd");
        map.putIfAbsent("inboundSeqLen", "3");
        map.putIfAbsent("outboundPrefix", "OUT");
        map.putIfAbsent("outboundDateFmt", "yyyyMMdd");
        map.putIfAbsent("outboundSeqLen", "3");
        return map;
    }

    @Override
    public String generateInboundNo() {
        Map<String, String> s = loadAll();
        String prefix = s.get("inboundPrefix");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(s.get("inboundDateFmt"));
        int seqLen = Integer.parseInt(s.get("inboundSeqLen"));
        return generateNo(prefix, fmt, seqLen);
    }

    @Override
    public String generateOutboundNo() {
        Map<String, String> s = loadAll();
        String prefix = s.get("outboundPrefix");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(s.get("outboundDateFmt"));
        int seqLen = Integer.parseInt(s.get("outboundSeqLen"));
        return generateNo(prefix, fmt, seqLen);
    }

    private String generateNo(String prefix, DateTimeFormatter fmt, int seqLen) {
        String today = LocalDate.now().format(fmt);
        String like = prefix + today + "%";

        QueryWrapper<Inbound> qw = new QueryWrapper<>();
        qw.likeRight("inbound_no", prefix + today);
        qw.orderByDesc("inbound_no");
        qw.last("LIMIT 1");
        Inbound last = inboundMapper.selectOne(qw);

        // 同时查出库（用同一个前缀的情况极少但兜底）
        int seq = 1;
        if (last != null) {
            String lastNo = last.getInboundNo();
            String lastSeq = lastNo.substring((prefix + today).length());
            try {
                seq = Integer.parseInt(lastSeq) + 1;
            } catch (NumberFormatException ignored) {}
        }

        return prefix + today + String.format("%0" + seqLen + "d", seq);
    }
}

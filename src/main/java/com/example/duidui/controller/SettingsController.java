package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @PutMapping("/number-format")
    public Result<?> saveNumberFormat(@RequestBody Map<String, Object> body) {
        return settingsService.saveNumberFormat(body);
    }
}

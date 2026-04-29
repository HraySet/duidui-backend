package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.entity.User;
import com.example.duidui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        return userService.register(user.getUsername(), user.getPassword());
    }

    // ============ 管理员接口 ============

    @GetMapping("/page")
    public Result<?> page(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return userService.page(pageNum, pageSize, keyword);
    }

    @PutMapping("/role")
    public Result<?> updateRole(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer role = Integer.valueOf(body.get("role").toString());
        return userService.updateRole(id, role);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}

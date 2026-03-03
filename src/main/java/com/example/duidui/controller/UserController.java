package com.example.duidui.controller;

import com.example.duidui.common.Result;
import com.example.duidui.entity.User;

import com.example.duidui.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")

public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("test")
    public String insertUser()
    {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        user.setRole("test");
        userService.insert(user);
        return "插入成功";
    }

    @GetMapping("getUser")
    public List<User> getUser()
    {
        return userService.selectList(null);
    }

    @PostMapping("/login")
    public  Result<Map<String, Object>> login(@RequestBody User user)
    {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user)
    {
        return userService.register(user.getUsername(),user.getPassword());
    }
}

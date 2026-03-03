package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.entity.User;


import java.util.List;
import java.util.Map;

public interface UserService {

    //登录
    Result<Map<String, Object>> login(String username, String password);

    //注册
    Result<String> register(String username, String password);

    void insert(User user);

    List<User> selectList(Object o);


}
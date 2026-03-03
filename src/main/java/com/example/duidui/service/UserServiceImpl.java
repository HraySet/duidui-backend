package com.example.duidui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.duidui.common.Result;
import com.example.duidui.entity.User;
import com.example.duidui.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    //用户登录
    public Result<Map<String, Object>> login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 加密用户输入的密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!user.getPassword().equals(encryptedPassword)) {
            return Result.error("密码错误");
        }

        String token = UUID.randomUUID().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("token", token);

        return Result.success(data);
    }


    @Override
    //用户注册
    public Result<String> register(String username, String password) {
        // 1. 查询用户名是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        User existUser = userMapper.selectOne(queryWrapper);

        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 2. 创建新用户对象
        User newUser = new User();
        newUser.setUsername(username);

        // 3. 密码加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        newUser.setPassword(encryptedPassword);

        // 4. 插入数据库
        int rows = userMapper.insert(newUser);

        if (rows > 0) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败");
        }
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public List<User> selectList(Object o) {
        return userMapper.selectList(null);
    }
}

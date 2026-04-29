package com.example.duidui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.duidui.common.Result;
import com.example.duidui.entity.User;
import com.example.duidui.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Token 存储：token -> username（临时放在内存里）
    private static final Map<String, String> TOKEN_STORE = new ConcurrentHashMap<>();

    @Override
    public Result<Map<String, Object>> login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!encoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = UUID.randomUUID().toString();
        TOKEN_STORE.put(token, username);

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("token", token);

        return Result.success(data);
    }

    @Override
    public Result<String> register(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setRole(0);
        newUser.setPassword(encoder.encode(password));

        int rows = userMapper.insert(newUser);
        if (rows > 0) {
            return Result.success();
        } else {
            return Result.error("注册失败");
        }
    }

    /**
     * 校验 token 是否有效
     */
    public boolean validateToken(String token) {
        return token != null && TOKEN_STORE.containsKey(token);
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public List<User> selectList(Object o) {
        return userMapper.selectList(null);
    }

    @Override
    public Result<?> page(int pageNum, int pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("username", keyword);
        }
        wrapper.orderByDesc("created_at");
        // 不暴露密码
        wrapper.select("id", "username", "role", "created_at", "updated_at");
        Page<User> result = userMapper.selectPage(page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<?> updateRole(Long id, Integer role) {
        if (id == null || role == null) {
            return Result.error("参数不能为空");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
        return Result.success();
    }

    @Override
    public Result<?> deleteUser(Long id) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        userMapper.deleteById(id);
        return Result.success();
    }
}

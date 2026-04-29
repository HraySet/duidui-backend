package com.example.duidui.service;

import com.example.duidui.common.Result;
import com.example.duidui.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    Result<Map<String, Object>> login(String username, String password);

    Result<String> register(String username, String password);

    /** 用户分页列表（管理员用，不暴露密码） */
    Result<?> page(int pageNum, int pageSize, String keyword);

    /** 修改用户角色 */
    Result<?> updateRole(Long id, Integer role);

    /** 删除用户 */
    Result<?> deleteUser(Long id);

    void insert(User user);

    List<User> selectList(Object o);

    boolean validateToken(String token);
}
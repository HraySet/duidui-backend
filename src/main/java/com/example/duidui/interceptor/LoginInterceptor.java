package com.example.duidui.interceptor;

import com.example.duidui.common.Result;
import com.example.duidui.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        // 去掉 Bearer 前缀（前端 axios 拦截器自动加的）
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 真校验：查一下 token 是不是咱发的
        if (StringUtils.hasText(token) && userService.validateToken(token)) {
            return true;
        }

        response.setContentType("application/json;charset=utf-8");
        Result<?> error = Result.error(401, "请先登录");
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
        return false;
    }
}

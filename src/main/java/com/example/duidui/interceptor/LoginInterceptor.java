package com.example.duidui.interceptor;

import com.example.duidui.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        
        // 简单校验一下 Token（毕设里判断有 token 就行，生产环境要校验 JWT）
        if (StringUtils.hasText(token)) {
            return true;
        }

        response.setContentType("application/json;charset=utf-8");
        Result<?> error = Result.error(401, "请先登录");
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
        return false;
    }
}

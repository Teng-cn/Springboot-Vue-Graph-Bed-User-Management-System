package com.imagehosting.web.controller;

import com.imagehosting.common.result.Result;
import com.imagehosting.common.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试接口
     *
     * @return 测试结果
     */
    @GetMapping("/hello")
    public Result<Map<String, String>> hello() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "Hello World!");
        return Result.success(data);
    }

    @GetMapping("/headers")
    public Result<Map<String, String>> headers(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        headers.put("Authorization", authHeader != null ? authHeader : "null");
        
        // 获取所有请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        
        return Result.success(headers);
    }
    
    /**
     * 测试认证状态
     */
    @GetMapping("/auth-status")
    public Result<Map<String, Object>> authStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 获取认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            status.put("authenticated", authentication.isAuthenticated());
            status.put("principal", authentication.getPrincipal());
            status.put("principalType", authentication.getPrincipal() != null ? 
                    authentication.getPrincipal().getClass().getName() : "null");
            status.put("authorities", authentication.getAuthorities());
            status.put("details", authentication.getDetails());
            
            // 尝试获取用户ID
            try {
                Long userId = SecurityUtil.getCurrentUserId();
                status.put("userId", userId);
                status.put("userIdSuccess", true);
            } catch (Exception e) {
                status.put("userIdSuccess", false);
                status.put("userIdError", e.getMessage());
                status.put("userIdErrorType", e.getClass().getName());
            }
        } else {
            status.put("authenticated", false);
            status.put("error", "No authentication object found");
        }
        
        return Result.success(status);
    }
} 
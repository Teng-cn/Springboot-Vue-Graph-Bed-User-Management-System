package com.imagehosting.web.controller;

import com.imagehosting.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试用户控制器
 */
@RestController
@RequestMapping("/test-user")
public class TestUserController {

    /**
     * 获取用户信息
     *
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        
        // 创建测试数据
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", "test_user");
        user.put("nickname", "测试用户");
        user.put("email", "test@example.com");
        user.put("avatar", "https://example.com/avatar.jpg");
        user.put("authHeader", authHeader);
        
        return Result.success(user);
    }

    /**
     * 获取用户统计信息
     *
     * @return 用户统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats() {
        // 创建测试数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("imageCount", 10);
        stats.put("viewCount", 100);
        stats.put("diskUsage", "10MB");
        stats.put("lastLoginTime", "2023-05-26 12:00:00");
        
        return Result.success(stats);
    }
} 
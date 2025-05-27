package com.imagehosting.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imagehosting.common.result.Result;
import com.imagehosting.common.util.JwtUtil;
import com.imagehosting.model.dto.UserLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证过滤器
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/auth/login");
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * 尝试认证
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 从请求中获取登录信息
            UserLoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), UserLoginDTO.class);
            
            // 创建认证令牌
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(), loginDTO.getPassword());
            
            // 设置认证详情
            setDetails(request, authRequest);
            
            // 执行认证
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            log.error("认证失败: {}", e.getMessage(), e);
            throw new RuntimeException("认证失败", e);
        }
    }

    /**
     * 认证成功
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                           FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 获取用户信息
        Long userId = (Long) authResult.getPrincipal();
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(userId.toString());
        
        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader(jwtUtil.getHeader(), jwtUtil.getTokenPrefix() + token);
        
        // 返回认证成功结果
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenPrefix", jwtUtil.getTokenPrefix());
        
        response.getWriter().write(objectMapper.writeValueAsString(Result.success("登录成功", tokenMap)));
    }

    /**
     * 认证失败
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                             AuthenticationException failed) throws IOException, ServletException {
        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 返回认证失败结果
        response.getWriter().write(objectMapper.writeValueAsString(Result.failed("用户名或密码错误")));
    }
} 